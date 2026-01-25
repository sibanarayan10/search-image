package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.ImageEngagement;
import com.searchimage.search_image.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageEngagementRepository extends JpaRepository<ImageEngagement,Long> {

    boolean existsByUserAndImage(User user, Image image);

    Optional<ImageEngagement> findByUserAndImage(User user, Image image);
//    Optional<ImageEngagement> findByUserIdAndImgId(Long userId, Long imgId);
}
