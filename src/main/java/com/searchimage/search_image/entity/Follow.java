package com.searchimage.search_image.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "follows",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"followed_by_id", "following_id"})
        }
)
public class Follow extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private Instant createdOn;

    @Column
    private Instant updatedOn;

    @Column(nullable = false)
    private boolean isActive;

    // User who initiates the follow
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_by_id", nullable = false)
    private User followedBy;

    // User who is being followed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @PrePersist
    protected void onCreate() {
        this.createdOn = Instant.now();
        this.isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = Instant.now();
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
        this.updatedOn = updatedOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(User followedBy) {
        this.followedBy = followedBy;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

}

