package com.searchimage.search_image.service;

import com.searchimage.search_image.dto.CommentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    boolean editComment(Long commentId, String newValue);
    boolean deleteComment(Long commentId);
    CommentResponse createComment(String comment,Long imageId);
    List<CommentResponse> getComments(Long imageId);
}
