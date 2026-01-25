package com.searchimage.search_image.entity;

import com.searchimage.search_image.entity.enums.AiStatus;
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

    /* ---------- USER REFERENCE (HYBRID MODEL) ---------- */

    // Stores only the ID (used in writes, Kafka, security)
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    // Optional JPA relationship (used only when needed)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "uploaded_by",
            insertable = false,
            updatable = false
    )
    private User user;

    /* ---------- METADATA ---------- */

    @Column(nullable = false, updatable = false)
    private Instant createdOn;

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


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiStatus aiRecordStatus;


    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageEngagement> imageEngagements;




    public List<ImageEngagement> getImageEngagements() {
        return imageEngagements;
    }

    public void setImageEngagements(List<ImageEngagement> imageEngagements) {
        this.imageEngagements = imageEngagements;
    }



    /* ---------- JPA lifecycle ---------- */

    @PrePersist
    private void onCreate() {
        this.createdOn = Instant.now();
        if (this.recordStatus == null) {
            this.recordStatus = RecordStatus.ACTIVE;
            this.aiRecordStatus=AiStatus.PENDING;
        }
    }

    /* ---------- Getters & Setters ---------- */
    public AiStatus getAiRecordStatus() {
        return aiRecordStatus;
    }

    public void setAiRecordStatus(AiStatus aiRecordStatus) {
        this.aiRecordStatus = aiRecordStatus;
    }
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

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getUser() {
        return user;
    }

    public Instant getCreatedOn() {
        return createdOn;
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

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }
}
