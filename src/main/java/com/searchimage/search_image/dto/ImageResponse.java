package com.searchimage.search_image.dto;

import com.searchimage.search_image.entity.Image;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;


@Component
    public class ImageResponse {
        private Long imageId;
        private String imageUrl;
        private String name;
        private String description;
        private String uploadedBy;
        private Instant uploadedOn;
        private long totalLikes;
        private boolean likedByCurrentUser;

public ImageResponse(){

}
    public ImageResponse(
            Long id,
            String name,
            String imgUrl,
            String description,

            Instant createdOn
    ) {
        this.imageId = id;
        this.name = name;
        this.imageUrl = imgUrl;
        this.description = description;
        this.uploadedOn = createdOn;
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

        public String getUploadedBy() {
            return uploadedBy;
        }

        public void setUploadedBy(String uploadedBy) {
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

