package com.searchimage.search_image.dto;

import java.time.Instant;

public interface ImageProjection {

    Long getId();
    String getName();
    String getImgUrl();
    String getDescription();
    Long getUploadedBy();
    Instant getCreatedOn();
    String getUploadedByUsername();
    Long getTotalLikes();
    Boolean getLikedByMe();
    Boolean getIsFollowing();
    Boolean getSavedByMe();

}
