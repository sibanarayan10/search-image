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
                        u.name       AS uploaded_by_username,
                        i.created_on,
                        COUNT(DISTINCT ie_like.id)    AS total_likes,
                        COUNT(DISTINCT i_comment.id)  AS total_comments,
                        COALESCE(ts_rank(i.search_vector,
                                 plainto_tsquery('english', :q)), 0)  AS rank,
                    
                        CASE WHEN SUM(CASE WHEN ie_user.liked = true  THEN 1 ELSE 0 END) > 0
                             THEN true ELSE false END AS liked_by_me,
                    
                        CASE WHEN SUM(CASE WHEN ie_user.saved = true  THEN 1 ELSE 0 END) > 0
                             THEN true ELSE false END AS saved_by_me,
                    
                        CASE WHEN COUNT(f.id) > 0
                             THEN true ELSE false END AS is_following
                    
                    FROM images i
                    
                    LEFT JOIN users u
                           ON u.id = i.uploaded_by
                    
                    LEFT JOIN follows f
                           ON f.followed_by_id = :userId
                          AND f.following_id   = i.uploaded_by
                          AND f.is_active       = true
                    
                    LEFT JOIN image_engagement ie_like
                           ON ie_like.image_id = i.id
                          AND ie_like.liked    = true
                    
                    LEFT JOIN image_engagement ie_user     
                           ON ie_user.image_id = i.id
                          AND ie_user.user_id  = :userId
                    
                    LEFT JOIN comments i_comment
                           ON i_comment.commented_on_id = i.id
                          AND i_comment.is_active       = true
                    
                    WHERE i.record_status = 'ACTIVE'
                      AND (:q IS NULL OR :q = ''
                           OR i.search_vector @@ plainto_tsquery('english', :q))
                      AND (:userSpecific = false OR i.uploaded_by = :userId)
                      AND (:likedOnly    = false OR ie_user.liked  = true)
                      AND (:savedOnly    = false OR ie_user.saved   = true)
                    
                    GROUP BY i.id, u.name
                    
                    ORDER BY rank DESC, i.created_on DESC                                                                        
                    """,

            countQuery = """
                    SELECT COUNT(DISTINCT i.id)
                       FROM images i
                       LEFT JOIN image_engagement ie_filter
                              ON ie_filter.image_id = i.id
                             AND ie_filter.user_id = :userId
                       
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
                         AND (
                               :likedOnly IS FALSE
                            OR ie_filter.liked = true
                         )
                         AND (
                               :savedOnly IS FALSE
                            OR ie_filter.saved = true
                         )
                                       
                    """,

            nativeQuery = true
    )
    Page<ImageProjection> searchImages(
            @Param("q") String query,
            @Param("userId") Long userId,
            @Param("userSpecific") boolean userSpecific,
            @Param("likedOnly") boolean likedOnly,
            @Param("savedOnly") boolean savedOnly,
            Pageable pageable
    );


}
