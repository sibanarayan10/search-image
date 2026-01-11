package com.searchimage.search_image.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageUploadRequest {

    private MultipartFile file;
    private String title;
    private String description;
    private List<String> tags;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // getters & setters
}