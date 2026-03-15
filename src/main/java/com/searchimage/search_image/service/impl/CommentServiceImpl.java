package com.searchimage.search_image.service.impl;

import com.searchimage.search_image.dto.CommentResponse;
import com.searchimage.search_image.entity.Comment;
import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.entity.enums.CommentType;
import com.searchimage.search_image.exception.ResourceNotFoundException;
import com.searchimage.search_image.exception.UnauthorizedException;
import com.searchimage.search_image.repository.CommentRepository;
import com.searchimage.search_image.repository.ImageRepository;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.service.CommentService;
import com.searchimage.search_image.utility.JWTUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository  commentRepository;
    private final UserRepository     userRepository;
    private final ImageRepository imageRepository;
    private final JWTUtility utils;

    @Override
    public CommentResponse createComment(String comment,Long imageId) {
        Long userId=utils.getCurrentUser().getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userId));

        if(comment.trim().isEmpty())throw new RuntimeException("Comment can't be empty");

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image not found: " + imageId));

        Comment nc = Comment.builder()
                .commentedBy(user)
                .commentedOnId(imageId)
                .comment(comment)
                .isActive(true)
                .type(CommentType.Image)
                .build();

        commentRepository.save(nc);

        return new CommentResponse(
                user.getId(),
                user.getName(),
                user.getImgUrl(),
                nc.getId(),
                comment,
                nc.getCreatedOn()
        );
    }

    @Override
    public boolean editComment(Long commentId,
                               String newValue) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found: " + commentId));

        Long currUserId=utils.getCurrentUser().getUserId();

        if (!comment.isActive())
            throw new ResourceNotFoundException("Comment not found: " + commentId);

        if (!comment.getCommentedBy().getId().equals(currUserId))
            throw new UnauthorizedException("You can only edit your own comments");

        if (newValue == null || newValue.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty");

        comment.setComment(newValue.trim());
        commentRepository.save(comment);
        return true;
    }

    @Override
    public boolean deleteComment(Long commentId) {

        Long currUserId=utils.getCurrentUser().getUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found: " + commentId));

        if (!comment.isActive())
            throw new ResourceNotFoundException("Comment not found: " + commentId);

        Image image = imageRepository.findById(comment.getCommentedOnId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image not found for this comment"));

        boolean isCommentAuthor = comment.getCommentedBy()
                .getId()
                .equals(currUserId);

        boolean isImageOwner  = image.getUploadedBy()
                .equals(currUserId);

        if (!isCommentAuthor && !isImageOwner)
            throw new UnauthorizedException(
                    "Only the comment author or image owner can delete this comment");

        comment.setActive(false);
        commentRepository.save(comment);
        return true;
    }

    @Override
    public List<CommentResponse> getComments(Long imageId){

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image not found: " + imageId));

        return commentRepository.getComments(imageId);
    }
}
