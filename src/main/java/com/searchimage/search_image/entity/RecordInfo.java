package com.searchimage.search_image.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public abstract class RecordInfo {

    protected String createdBy;

    protected Long userId;

    protected Instant createdAt;

    protected Instant updatedAt;
}
