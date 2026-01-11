package com.searchimage.search_image.service;

import com.searchimage.search_image.dto.ImageResponse;
import com.searchimage.search_image.dto.ImageUploadRequest;
import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.security.UserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {


    void uploadImages(List<ImageUploadRequest> files);

    void deleteImage(Long imageId);
    List<ImageResponse> getImages(boolean isUserSpecific, boolean isSaved);
}

