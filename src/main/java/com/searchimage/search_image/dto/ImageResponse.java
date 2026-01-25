package com.searchimage.search_image.dto;

import org.springframework.stereotype.Component;

import java.time.Instant;


public class ImageResponse {
    private Long imageId;
    private String imageUrl;
    private String name;
    private String description;
    private Long uploadedBy;
    private Instant uploadedOn;
    private Long totalLikes;
    private boolean likedByCurrentUser;
    private boolean savedByCurrentUser;

    private boolean isFollowing;


    private String uploadedByUserName;

    public ImageResponse() {

    }

    public boolean isSavedByCurrentUser() {
        return savedByCurrentUser;
    }

    public void setSavedByCurrentUser(boolean savedByCurrentUser) {
        this.savedByCurrentUser = savedByCurrentUser;
    }

    public ImageResponse(
            Long id,
            String name,
            String imgUrl,
            String description,
            Long totalLikes,
            boolean likedByCurrentUser,
            Instant createdOn,
            String uploadedByUserName,
            boolean isFollowing,
            boolean savedByCurrentUser


    ) {
        this.imageId = id;
        this.name = name;
        this.imageUrl = imgUrl;
        this.description = description;
        this.uploadedOn = createdOn;
        this.totalLikes = totalLikes;
        this.likedByCurrentUser = likedByCurrentUser;
        this.uploadedByUserName=uploadedByUserName;
        this.isFollowing=isFollowing;
        this.savedByCurrentUser=savedByCurrentUser;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getUploadedByUserName() {
        return uploadedByUserName;
    }

    public void setUploadedByUserName(String uploadedByUserName) {
        this.uploadedByUserName = uploadedByUserName;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Instant getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Instant uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
}

