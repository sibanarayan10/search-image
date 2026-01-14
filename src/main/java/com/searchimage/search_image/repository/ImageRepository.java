package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.enums.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query(
            value = """
        SELECT *
        FROM images
        WHERE record_status = 'ACTIVE'
        AND search_vector @@ plainto_tsquery(:q)
        ORDER BY ts_rank(search_vector, plainto_tsquery(:q)) DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM images
        WHERE record_status = 'ACTIVE'
        AND search_vector @@ plainto_tsquery(:q)
        """,
            nativeQuery = true
    )
    Page<Image> searchImages(
            @Param("q") String query,
            Pageable pageable
    );
}
