package com.searchimage.search_image.service.impl;

import com.cloudinary.Cloudinary;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.searchimage.search_image.dto.*;
import com.searchimage.search_image.entity.*;
import com.searchimage.search_image.entity.enums.RecordStatus;
import com.searchimage.search_image.exception.ResourceNotFoundException;
import com.searchimage.search_image.exception.UnauthorizedException;
import com.searchimage.search_image.repository.ImageRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.ImageService;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;
    private final JPAQueryFactory queryFactory;

    public ImageServiceImpl(
            ImageRepository imageRepository,
            Cloudinary cloudinary,
            JPAQueryFactory queryFactory
    ) {
        this.imageRepository = imageRepository;
        this.cloudinary = cloudinary;
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional
    public void uploadImages(List<ImageUploadRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("No images provided for upload");
        }

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
        }
    }

    @Override
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        UserPrincipal principal = getCurrentUser();

        // Ownership check
        if (!image.getUploadedBy().equals(principal.getUserId())) {
            throw new UnauthorizedException("You are not authorized to delete this image");
        }

        deleteFromCloudinary(image.getImgUrl());
        imageRepository.delete(image);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ImageResponse> searchImages(
            String query,
            boolean userSpecific,
            boolean likedOnly,
            boolean savedOnly,
            int page,
            int size
    ) {
        Optional<UserPrincipal> currentUser = getCurrentUserOpt();
        Long userId = currentUser.map(UserPrincipal::getUserId).orElse(null);

        Pageable pageable = PageRequest.of(page, size);

        // If user is unauthenticated and requests user-scoped data, safely return an empty page
        if (userId == null && (userSpecific || likedOnly || savedOnly)) {
            return new PageResponse<>(new PageImpl<>(Collections.emptyList(), pageable, 0));
        }

        QImage image = QImage.image;
        QUser uploader = new QUser("uploader");
        QImageEngagement engagementLike = new QImageEngagement("engagementLike");
        QComment comment = new QComment("comment");
        QImageEngagement userEngagement = new QImageEngagement("userEngagement");
        QFollow follow = new QFollow("follow");

        // Subquery for total likes
        Expression<Long> totalLikesExpr = JPAExpressions
                .select(engagementLike.count())
                .from(engagementLike)
                .where(engagementLike.image.id.eq(image.id).and(engagementLike.liked.isTrue()));

        // Subquery for total comments
        Expression<Long> totalCommentsExpr = JPAExpressions
                .select(comment.count())
                .from(comment)
                .where(comment.commentedOnId.eq(image.id).and(comment.isActive.isTrue()));

        // Expressions for current user interactions (evaluated only when authenticated)
        Expression<Boolean> likedByMeExpr = (userId != null)
                ? JPAExpressions
                .selectOne()
                .from(userEngagement)
                .where(userEngagement.image.id.eq(image.id)
                        .and(userEngagement.user.id.eq(userId))
                        .and(userEngagement.liked.isTrue()))
                .exists()
                : Expressions.asBoolean(false);

        Expression<Boolean> savedByMeExpr = (userId != null)
                ? JPAExpressions
                .selectOne()
                .from(userEngagement)
                .where(userEngagement.image.id.eq(image.id)
                        .and(userEngagement.user.id.eq(userId))
                        .and(userEngagement.saved.isTrue()))
                .exists()
                : Expressions.asBoolean(false);

        Expression<Boolean> isFollowingExpr = (userId != null)
                ? JPAExpressions
                .selectOne()
                .from(follow)
                .where(follow.followedBy.id.eq(userId)
                        .and(follow.following.id.eq(image.uploadedBy))
                        .and(follow.isActive.isTrue()))
                .exists()
                : Expressions.asBoolean(false);

        // Build dynamic WHERE clause
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(image.recordStatus.eq(RecordStatus.ACTIVE));

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim();
            builder.and(
                    image.name.containsIgnoreCase(q)
                            .or(image.description.containsIgnoreCase(q))
                            .or(image.tags.any().containsIgnoreCase(q))
            );
        }

        if (userSpecific && userId != null) {
            builder.and(image.uploadedBy.eq(userId));
        }

        if (likedOnly && userId != null) {
            builder.and(
                    JPAExpressions
                            .selectOne()
                            .from(userEngagement)
                            .where(userEngagement.image.id.eq(image.id)
                                    .and(userEngagement.user.id.eq(userId))
                                    .and(userEngagement.liked.isTrue()))
                            .exists()
            );
        }

        if (savedOnly && userId != null) {
            builder.and(
                    JPAExpressions
                            .selectOne()
                            .from(userEngagement)
                            .where(userEngagement.image.id.eq(image.id)
                                    .and(userEngagement.user.id.eq(userId))
                                    .and(userEngagement.saved.isTrue()))
                            .exists()
            );
        }

        Long totalCount = queryFactory
                .select(image.count())
                .from(image)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        if (total == 0) {
            return new PageResponse<>(new PageImpl<>(Collections.emptyList(), pageable, 0));
        }

        List<Tuple> results = queryFactory
                .select(
                        image.id,
                        image.name,
                        image.imgUrl,
                        image.description,
                        image.uploadedBy,
                        uploader.name,
                        image.createdOn,
                        totalLikesExpr,
                        totalCommentsExpr,
                        likedByMeExpr,
                        savedByMeExpr,
                        isFollowingExpr
                )
                .from(image)
                .leftJoin(image.user, uploader)
                .where(builder)
                .orderBy(image.createdOn.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ImageResponse> content = results.stream().map(tuple -> {
            ImageResponse dto = new ImageResponse();
            dto.setImageId(tuple.get(image.id));
            dto.setName(tuple.get(image.name));
            dto.setImageUrl(tuple.get(image.imgUrl));
            dto.setDescription(tuple.get(image.description));
            dto.setUploadedBy(tuple.get(image.uploadedBy));
            dto.setUploadedByUserName(tuple.get(uploader.name));
            dto.setUploadedOn(tuple.get(image.createdOn));

            Long likes = tuple.get(totalLikesExpr);
            dto.setTotalLikes(likes != null ? likes : 0L);

            Long comments = tuple.get(totalCommentsExpr);
            dto.setTotalComments(comments != null ? comments : 0L);

            Boolean liked = tuple.get(likedByMeExpr);
            dto.setLikedByCurrentUser(Boolean.TRUE.equals(liked));

            Boolean saved = tuple.get(savedByMeExpr);
            dto.setSavedByCurrentUser(Boolean.TRUE.equals(saved));

            Boolean following = tuple.get(isFollowingExpr);
            dto.setFollowing(Boolean.TRUE.equals(following));

            return dto;
        }).toList();

        return new PageResponse<>(new PageImpl<>(content, pageable, total));
    }

    @Override
    public byte[] fetchFromCloudinary(Long imgId) {
        Image image = imageRepository.findById(imgId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imgId));
        String cloudinaryUrl = image.getImgUrl();
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(cloudinaryUrl, byte[].class);
    }

    // -------- Helpers --------

    private String uploadToCloudinary(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), Map.of());
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    private void deleteFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed", e);
        }
    }

    private Optional<UserPrincipal> getCurrentUserOpt() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    private UserPrincipal getCurrentUser() {
        return getCurrentUserOpt()
                .orElseThrow(() -> new UnauthorizedException("User is not authenticated"));
    }

    private String extractPublicId(String imageUrl) {
        return imageUrl
                .substring(imageUrl.lastIndexOf("/") + 1,
                        imageUrl.lastIndexOf("."));
    }
}
