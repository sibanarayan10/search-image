package com.searchimage.search_image.service.impl;

import com.searchimage.search_image.entity.Follow;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.repository.FollowRepository;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowServiceImpl(FollowRepository followRepository,UserRepository userRepository){
        this.userRepository=userRepository;
        this.followRepository=followRepository;
    }

    @Override
    public boolean toggleFollow(Long userId) {
        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long authUserId=up.getUserId();
        if(authUserId==userId){
            throw new RuntimeException("Followig and follower can't be same");
        }
        User following=userRepository.findById(authUserId).orElseThrow(()->new RuntimeException("User not found"));
        User followedBy=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));

        Optional<Follow> existing=followRepository.findByFollowedByIdAndFollowingId(authUserId,userId);
        if(existing.isPresent()){
            Follow doc=existing.get();
            boolean isActive=doc.isActive();
            doc.setActive(!isActive);
            followRepository.save(doc);
        }else{
            Follow f=new Follow();
            f.setFollowing(followedBy);
            f.setFollowedBy(following);
            followRepository.save(f);
        }
        return true;

    }
}
