package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Long> {
    Optional<Follow> findByFollowedByIdAndFollowingId(
            Long followedById,
            Long followingId
    );

    long countByFollowingIdAndIsActiveTrue(Long userId);

    long countByFollowedByIdAndIsActiveTrue(Long userId);
}
