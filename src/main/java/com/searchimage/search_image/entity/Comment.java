package com.searchimage.search_image.entity;

import com.searchimage.search_image.entity.enums.CommentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="comments")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity{

    @Column(name="comment",nullable = false)
    public String comment;

    @Column(name="type",nullable = false)
    @Enumerated(EnumType.STRING)
    public CommentType type;

    @JoinColumn(nullable = false, updatable = false,name="commented_by")
    @ManyToOne(fetch = FetchType.LAZY)
    public User commentedBy;

    @Column(name = "commented_on_id", nullable = false, updatable = false)
    public Long commentedOnId;

    @Column(nullable = false, updatable = false,name="created_on")
    public Instant createdOn;

    @Column(name="updated_on")
    public Instant updatedOn;

    @Column(nullable = false,name="is_active")
    public boolean isActive;


    @PrePersist
    public void onSave(){
        createdOn=Instant.now();
        isActive=true;
    }

    @PreUpdate
    public void onUpdate(){
        updatedOn=Instant.now();
    }
}
