package com.searchimage.search_image.entity;

import com.searchimage.search_image.entity.enums.RecordStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "images")
public class Image extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false, updatable = false)
    private Instant uploadedOn;

    @Column(nullable = false, length = 100)
    private String uploadedBy;

    @Column(nullable = false)
    private Long uploadedId;

    @Column(length = 500)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "image_tags",
            joinColumns = @JoinColumn(name = "image_id")
    )
    @Column(name = "tag")
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus recordStatus;

    /* ---------- JPA lifecycle callbacks ---------- */

@PrePersist
private void onCreate(){
    this.uploadedOn=Instant.now();
    if(this.recordStatus==null){
        this.recordStatus=RecordStatus.ACTIVE;
    }

}

    /* ---------- Getters & Setters ---------- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Instant getUploadedOn() {
        return uploadedOn;
    }
    public void setUploadedOn() {
        this.uploadedOn = Instant.now();
    }
    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Long getUploadedId() {
        return uploadedId;
    }

    public void setUploadedId(Long uploadedId) {
        this.uploadedId = uploadedId;
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
}
