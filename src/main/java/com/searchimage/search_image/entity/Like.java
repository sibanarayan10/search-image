package com.searchimage.search_image.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="Likes")
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long imgId;

    @Column(nullable = false)
    private Instant likedOn;

    @Column(nullable = false)
    private boolean isActive;

    @PrePersist
    private void onCreate(){
        this.likedOn=Instant.now();
        this.isActive=true;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public Instant getLikedOn() {
        return likedOn;
    }

    public void setLikedOn(Instant likedOn) {
        this.likedOn = likedOn;
    }
}
