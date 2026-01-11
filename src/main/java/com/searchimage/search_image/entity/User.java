package com.searchimage.search_image.entity;

import com.searchimage.search_image.entity.enums.RecordStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
public class User extends BaseEntity implements Serializable {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdOn;

    @Column
    private String imgUrl;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus recordStatus;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    /* ---------- JPA lifecycle callbacks ---------- */

    @PrePersist
    protected void onCreate() {
        this.createdOn = Instant.now();
        if (this.recordStatus == null) {
            this.recordStatus = RecordStatus.ACTIVE;
        }
    }

    /* ---------- Getters & Setters ---------- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // password should ALWAYS be hashed before setting
    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }
}

