package com.searchimage.search_image.dto;

import java.time.Instant;

public record CommentResponse(Long userId,
                              String username,
                              String userImg,
                              Long commentId,
                              String comment,
                              Instant createdOn) { }

