package com.searchimage.search_image.repository;

import com.searchimage.search_image.dto.ImageProjection;
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
public interface ImageRepository extends JpaRepository<Image, Long> {
    // All images
    List<Image> findAllByRecordStatus(RecordStatus status);

    // Images uploaded by user
    List<Image> findByUploadedByAndRecordStatus(
            Long userId,
            RecordStatus status
    );

    @Query(
            value = """
                    SELECT
                        i.id,
                        i.name,
                        i.img_url,
                        i.description,
                                      
                        i.uploaded_by,
                        u.name AS uploaded_by_username,
                                      
                        i.created_on,
                                      
                        COUNT(l.id) AS total_likes,
                                      
                        CASE
                            WHEN SUM(
                                CASE
                                    WHEN :userId IS NOT NULL
                                     AND l.user_id = :userId
                                     AND l.is_active = true
                                    THEN 1
                                    ELSE 0
                                END
                            ) > 0
                            THEN true
                            ELSE false
                        END AS liked_by_me,
                                      
                        CASE
                            WHEN COUNT(f.id) > 0
                            THEN true
                            ELSE false
                        END AS is_following
                                      
                    FROM images i
                                      
                    LEFT JOIN users u
                           ON u.id = i.uploaded_by
                                      
                    LEFT JOIN follows f
                           ON f.followed_by_id = :userId
                          AND f.following_id = i.uploaded_by 
                          And f.is_active=TRUE
                                      
                    LEFT JOIN likes l
                           ON l.img_id = i.id
                          AND l.is_active = true
                                      
                    WHERE i.record_status = 'ACTIVE'
                      AND (
                            :q IS NULL
                         OR :q = ''
                         OR i.search_vector @@ plainto_tsquery(:q)
                      )
                      AND (
                            :userSpecific IS FALSE
                         OR i.uploaded_by = :userId
                      )
                                      
                    GROUP BY
                        i.id,
                        u.name
                                      
                    ORDER BY
                        CASE
                            WHEN :q IS NULL OR :q = '' THEN 0
                            ELSE ts_rank(i.search_vector, plainto_tsquery(:q))
                        END DESC,
                        i.created_on DESC                                                                          
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT i.id)
                    FROM images i
                    WHERE i.record_status = 'ACTIVE'
                      AND (
                            :q IS NULL
                         OR :q = ''
                         OR i.search_vector @@ plainto_tsquery(:q)
                      )
                      AND (
                            :userSpecific IS FALSE
                         OR i.uploaded_by = :userId
                      )
                                       
                    """,

            nativeQuery = true
    )
    Page<ImageProjection> searchImages(
            @Param("q") String query,
            @Param("userId") Long userId,
            @Param("userSpecific") boolean userSpecific,
            Pageable pageable
    );


}
