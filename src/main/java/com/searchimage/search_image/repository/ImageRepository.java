package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    // All images
    List<Image> findAllByRecordStatus(RecordStatus status);

    // Images uploaded by user
    List<Image> findByUploadedByAndRecordStatus(
            Long userId,
            RecordStatus status
    );
}
