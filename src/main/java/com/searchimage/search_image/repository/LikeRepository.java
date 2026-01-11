package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByUserIdAndImgId(Long userId, Long imgId);

    List<Like> findByUserIdAndImgIdIn(Long userId,List<Long>imgIds);
    @Query("""
        SELECT l.imgId, COUNT(l)
        FROM Like l
        WHERE l.imgId IN :imageIds
        GROUP BY l.imgId
    """)
    List<Object[]> countLikesByImageIds(
            @Param("imageIds") List<Long> imageIds
    );
}
