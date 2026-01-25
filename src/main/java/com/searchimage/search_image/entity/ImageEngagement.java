package com.searchimage.search_image.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Table(
        name = "image_engagement",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "image_id"})
        }
)
@Entity
public class ImageEngagement extends BaseEntity{


    @Column(name="created_on",nullable = false)
    private Instant createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(nullable = false)
    private boolean liked = false;

    @Column(nullable = false)
    private boolean saved = false;

    @Column(name="updated_on")
    private Instant updatedOn;


    /**Getter and setter */

    public Instant getCreatedOn() {
        return createdOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    @PrePersist
    private void onCreate() {
        this.createdOn = Instant.now();
    }
    @PreUpdate
    private void onUpdate() {
        this.updatedOn = Instant.now();
    }
}
