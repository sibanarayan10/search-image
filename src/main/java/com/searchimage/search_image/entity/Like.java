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

    @Column(nullable = false,updatable = false)
    private Instant createdOn;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    private Instant updatedOn;



    @PrePersist
    private void onCreate(){
        this.createdOn=Instant.now();
        this.isActive=true;
    }
    @PreUpdate
    private void onUpdate() {
        this.updatedOn = Instant.now();
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

    public Instant getUpdatedOn() {
        return updatedOn;
    }


    public Instant getCreatedOn() {
        return createdOn;
    }


}
