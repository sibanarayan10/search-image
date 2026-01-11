package com.searchimage.search_image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchimage.search_image.dto.ImageResponse;
import com.searchimage.search_image.dto.ImageUploadRequest;
import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173",
        allowCredentials = "true")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // -------- Upload single image --------
//    @PostMapping("/upload")
//    public ResponseEntity<ImageResponse> uploadImage(
//            @RequestParam("file") MultipartFile file
//    ) {
//        Image image = imageService.uploadImage(file);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ImageResponse.from(image));
//    }

    // -------- Upload multiple images --------
    @PostMapping(
            value = "/user/addImages",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadImages(
            @RequestParam("images") List<MultipartFile> images,
            HttpServletRequest request
    ) {

        if (images == null || images.isEmpty()) {
            return ResponseEntity.badRequest().body("No images provided");
        }

        List<ImageUploadRequest> uploadRequests = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {

            String title = request.getParameter("metadata[" + i + "][title]");
            String desc  = request.getParameter("metadata[" + i + "][desc]");
            String tags  = request.getParameter("metadata[" + i + "][tags]");

            ImageUploadRequest dto = new ImageUploadRequest();
            dto.setFile(images.get(i));
            dto.setTitle(title);
            dto.setDescription(desc);
            dto.setTags(parseTags(tags));

            uploadRequests.add(dto);
        }

        imageService.uploadImages(uploadRequests);

        return ResponseEntity.ok("Images uploaded successfully");
    }
    private List<String> parseTags(String tagsJson) {
        if (tagsJson == null) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(tagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    // -------- Delete image --------
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId
    ) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("images")
    public ResponseEntity<List<ImageResponse>> getImages(@RequestParam boolean isUserSpecific, @RequestParam boolean isSaved){
            List<ImageResponse>images=imageService.getImages(isUserSpecific,isSaved);
            return ResponseEntity.ok(images);
    }
}

