package com.searchimage.search_image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchimage.search_image.dto.ImageResponse;
import com.searchimage.search_image.dto.ImageUploadRequest;
import com.searchimage.search_image.dto.PageResponse;
import com.searchimage.search_image.entity.enums.ImageEngagementType;
import com.searchimage.search_image.service.ImageEngagementService;
import com.searchimage.search_image.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:5173",
        allowCredentials = "true")
public class ImageController {

    private final ImageService imageService;
    private final ImageEngagementService imageEngagementService;

    public ImageController(ImageService imageService,ImageEngagementService imageEngagementService) {
        this.imageService = imageService;
        this.imageEngagementService=imageEngagementService;
    }

    // -------- Upload single image --------

    // -------- Upload multiple images --------
    @PostMapping(
            value = "/user/addImages",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadImages(
            @RequestParam("images") List<MultipartFile> images,
            @RequestPart("metadata") String metadataJson
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        List<ImageUploadRequest> metadata =
                mapper.readValue(
                        metadataJson,
                        new TypeReference<List<ImageUploadRequest>>() {}
                );

        if (images.size() != metadata.size()) {
            throw new IllegalArgumentException("Images & metadata mismatch");
        }
        List<ImageUploadRequest> uploadRequests = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            metadata.get(i).setFile(images.get(i));



            uploadRequests.add( metadata.get(i));
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
    @DeleteMapping("images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId
    ) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponse<ImageResponse>> searchImages(
                @RequestParam(value = "q",defaultValue = "") String query,
                @RequestParam("userSpecific") boolean userSpecific,
                @RequestParam(value = "likedOnly",defaultValue = "false") boolean likedOnly,
                @RequestParam(value = "savedOnly",defaultValue = "false") boolean savedOnly,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size
        ) {
            return ResponseEntity.ok(
                    imageService.searchImages(query,userSpecific,likedOnly,savedOnly, page, size)
            );
        }


        /**Image engagement related api*/

    @PostMapping("/images/{imgId}/engagements")
    public ResponseEntity<Boolean> handleEngagement(@PathVariable Long imgId,
                                              @RequestParam(value="type") ImageEngagementType type){
        boolean result= imageEngagementService.applyInteraction(imgId,type);
        return ResponseEntity.ok(result);
    }
}

