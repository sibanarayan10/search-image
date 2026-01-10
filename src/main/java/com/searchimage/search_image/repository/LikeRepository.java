package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByUserIdAndImgId(Long userId, Long imgId);

}
