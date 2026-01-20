package com.searchimage.search_image.service.impl;

import com.cloudinary.Cloudinary;
import com.searchimage.search_image.dto.ImageProjection;
import com.searchimage.search_image.dto.ImageResponse;
import com.searchimage.search_image.dto.ImageUploadRequest;
import com.searchimage.search_image.dto.PageResponse;
import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.Like;
import com.searchimage.search_image.entity.enums.RecordStatus;
import com.searchimage.search_image.entity.event.ImageUploadedEvent;
import com.searchimage.search_image.kafka.producer.ImageEventProducer;
import com.searchimage.search_image.repository.ImageRepository;
import com.searchimage.search_image.repository.LikeRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.ImageService;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;

    private final Cloudinary cloudinary;
    private final ImageEventProducer eventProducer;

    public ImageServiceImpl(
            ImageRepository imageRepository,
            Cloudinary cloudinary,
            ImageEventProducer eventProducer,
            LikeRepository likeRepository
    ) {
        this.imageRepository = imageRepository;
        this.cloudinary = cloudinary;
        this.eventProducer = eventProducer;
        this.likeRepository=likeRepository;
    }


    @Override
    @Transactional
    public void uploadImages(List<ImageUploadRequest> requests) {

        UserPrincipal user = getCurrentUser();

        for (ImageUploadRequest req : requests) {

            String imageUrl = uploadToCloudinary(req.getFile());

            Image image = new Image();
            image.setName(req.getTitle());
            image.setDescription(req.getDescription());
            image.setTags(req.getTags());
            image.setImgUrl(imageUrl);
            image.setUploadedBy(user.getUserId());

            imageRepository.save(image);

            eventProducer.publishImageUploaded(
                    new ImageUploadedEvent(image.getId(), imageUrl)
            );
        }
    }


    @Override
    public void deleteImage(Long imageId) {

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        UserPrincipal principal = getCurrentUser();

        // Optional: ownership check
        if (!image.getUploadedBy().equals(principal.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        deleteFromCloudinary(image.getImgUrl());
        imageRepository.delete(image);
    }



    @Override
//    public List<ImageResponse> getImages(boolean isUserSpecific,boolean isSaved){
//        UserPrincipal u=getCurrentUser();
//        Long userId=u.getUserId();
//        List<Image>images;
//        if(isUserSpecific){
//            images=imageRepository.findByUploadedByAndRecordStatus(userId, RecordStatus.ACTIVE);
//
//        }else{
//            images=imageRepository.findAllByRecordStatus(RecordStatus.ACTIVE);
//
//        }
//        List<Long> imageIds = images.stream()
//                .map(Image::getId)
//                .toList();
//
//        Map<Long, Long> likeCountMap = new HashMap<>();
//
//        for (Object[] row : likeRepository.countLikesByImageIds(imageIds)) {
//            Long imgId = (Long) row[0];
//            Long count = (Long) row[1];
//            likeCountMap.put(imgId, count);
//        }
//        List<Like> userLikes =
//                likeRepository.findByUserIdAndImgIdIn(
//                        userId,
//                        imageIds
//                );
//        Set<Long> likedImageIds = userLikes.stream()
//                .map(Like::getImgId)
//                .collect(Collectors.toSet());
//        List<ImageResponse> response = new ArrayList<>();
//
//        for (Image image : images) {
//            ImageResponse dto = new ImageResponse();
//
//            dto.setImageId(image.getId());
//            dto.setImageUrl(image.getImgUrl());
//            dto.setUploadedBy(image.getUser().getName());
//            dto.setUploadedOn(image.getCreatedOn());
//            dto.setTotalLikes(
//                    likeCountMap.getOrDefault(image.getId(), 0L)
//            );
//            dto.setLikedByCurrentUser(
//                    likedImageIds.contains(image.getId())
//            );
//
//            response.add(dto);
//        }
//
//        return response;
//    }
    public PageResponse<ImageResponse> searchImages(
            String query,
            boolean userSpecific,
            int page,
            int size
    ) {

        UserPrincipal u=getCurrentUser();
        Long userId=u.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ImageProjection> images=imageRepository
                .searchImages(query,userId,userSpecific,pageable);

        Page<ImageResponse> dtoPage = images.map(p -> {
            ImageResponse dto = new ImageResponse();
            dto.setImageId(p.getId());
            dto.setName(p.getName());
            dto.setImageUrl(p.getImgUrl());
            dto.setDescription(p.getDescription());
            dto.setUploadedBy(p.getUploadedBy());
            dto.setUploadedOn(p.getCreatedOn());
            dto.setTotalLikes(p.getTotalLikes());
            dto.setLikedByCurrentUser(p.getLikedByMe());
            dto.setUploadedBy(p.getUploadedBy());
            dto.setUploadedByUserName(p.getUploadedByUsername());
            dto.setFollowing(p.getIsFollowing());
            return dto;
        });
        return new PageResponse<ImageResponse>(dtoPage);

    }








    // -------- Helpers --------


//    private ImageResponse toResponse(Image image,Long totalLikes,boolean likedByUser) {
//        return new ImageResponse(
//                image.getId(),
//                image.getName(),
//              image.getImgUrl(),
//                image.getDescription(),
//                totalLikes,
//                likedByUser,
//                image.getCreatedOn()
//
//
//                );
//    }

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), Map.of());
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed");
        }
    }

    private void deleteFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed");
        }
    }

    private UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return (UserPrincipal) auth.getPrincipal();
    }

    private String extractPublicId(String imageUrl) {
        // Example: extract between last '/' and '.'
        return imageUrl
                .substring(imageUrl.lastIndexOf("/") + 1,
                        imageUrl.lastIndexOf("."));
    }
}

