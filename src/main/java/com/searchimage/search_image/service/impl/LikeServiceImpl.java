package com.searchimage.search_image.service.impl;

import com.searchimage.search_image.entity.Like;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.repository.LikeRepository;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.LikeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService{
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository,ModelMapper modelMapper){
        this.likeRepository=likeRepository;
        this.userRepository=userRepository;
    }

    @Override
    public boolean toggleLike(Long imgId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Long userId = principal.getUserId();

        // Check if a like already exists
        Optional<Like> existingLike = likeRepository.findByUserIdAndImgId(userId, imgId);
        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            // Toggle the active status
            like.setActive(!like.isActive());
            likeRepository.save(like);
            return like.isActive(); // Return the new status
        } else {
            // Create a new like and set active to true
            Like newLike = new Like();
            User user = userRepository.getReferenceById(userId);
            newLike.setUser(user);
            newLike.setImgId(imgId);
            likeRepository.save(newLike);
            return true;
        }
    }

}
