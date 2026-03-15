package com.searchimage.search_image.controller;

import com.searchimage.search_image.dto.CommentRequest;
import com.searchimage.search_image.dto.CommentResponse;
import com.searchimage.search_image.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images/{imageId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse>createComment(@PathVariable Long imageId,@RequestBody CommentRequest request){
        return ResponseEntity.ok(commentService.createComment(request.comment(),imageId));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>>getComments(@PathVariable Long imageId){
        return ResponseEntity.ok(commentService.getComments(imageId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Boolean>editComment(@PathVariable Long commentId,@RequestBody String newValue){
        return ResponseEntity.ok(commentService.editComment(commentId,newValue));
    }

    @PutMapping("/{commentId}/delete")
    public ResponseEntity<Boolean>deleteComment(@PathVariable Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }

}
