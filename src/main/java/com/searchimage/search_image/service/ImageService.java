package com.searchimage.search_image.service;

import com.searchimage.search_image.dto.ImageResponse;
import com.searchimage.search_image.dto.ImageUploadRequest;
import com.searchimage.search_image.dto.PageResponse;
import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {


    void uploadImages(List<ImageUploadRequest> files);

    void deleteImage(Long imageId);
//    List<ImageResponse> getImages(boolean isUserSpecific, boolean isSaved);
    PageResponse<ImageResponse>  searchImages(String query,
                                             boolean userSpecific,
                                             boolean likedOnly,boolean savedOnly,
                                             int page,
                                             int size);

//    boolean likeImage(Long imgId);
    
}

