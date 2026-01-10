package com.searchimage.search_image.controller;

import com.searchimage.search_image.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowController {
    private final FollowService followService;
    @Autowired
    public FollowController(FollowService followService){
        this.followService=followService;
    }
    @PostMapping("user/{userId}/toggleFollow")
    public ResponseEntity<Boolean> toggleFollow(Long userId){
        followService.toggleFollow(userId);
        return ResponseEntity.ok(true);
    }
}
