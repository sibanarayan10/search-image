package com.searchimage.search_image.controller;

import com.searchimage.search_image.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "https://localhost:5173")

public class FollowController {
    private final FollowService followService;
    @Autowired
    public FollowController(FollowService followService){
        this.followService=followService;
    }
    @PostMapping("user/{userId}/toggleFollow")
    public ResponseEntity<Boolean> toggleFollow(@PathVariable Long userId){
        followService.toggleFollow(userId);
        return ResponseEntity.ok(true);
    }
}
