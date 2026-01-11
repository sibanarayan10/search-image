package com.searchimage.search_image.entity.event;

public class ImageUploadedEvent {

    private Long imageId;
    private String imageUrl;

    public ImageUploadedEvent() {}

    public ImageUploadedEvent(Long imageId, String imageUrl) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }

    public Long getImageId() { return imageId; }
    public String getImageUrl() { return imageUrl; }
}
