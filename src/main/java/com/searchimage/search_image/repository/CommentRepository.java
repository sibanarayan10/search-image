package com.searchimage.search_image.repository;

import com.searchimage.search_image.dto.CommentResponse;
import com.searchimage.search_image.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("""
            SELECT new com.searchimage.search_image.dto.CommentResponse(
                u.id,
                u.name,
                u.imgUrl,
                c.id,
                c.comment,
                c.createdOn
                
            )
            FROM Comment c
            JOIN c.commentedBy u
            WHERE c.commentedOnId = :imageId
              AND c.isActive = true
            ORDER BY c.createdOn DESC
            """)
    List<CommentResponse> getComments(@Param("imageId") Long imageId);



}
