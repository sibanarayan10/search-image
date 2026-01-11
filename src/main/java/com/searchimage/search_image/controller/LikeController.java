package com.searchimage.search_image.controller;

import com.searchimage.search_image.service.LikeService;
import com.searchimage.search_image.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://localhost:5173")

@RequestMapping("/api/v1")

public class LikeController {
    private final LikeService likeService;
    public LikeController(LikeService likeService){
        this.likeService=likeService;
    }
    @PostMapping("images/{imgId}/toggleLike")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long imgId){
        boolean result= likeService.toggleLike(imgId);
        return ResponseEntity.ok(true);
    }
}
