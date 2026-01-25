package com.searchimage.search_image.service.impl;

import com.searchimage.search_image.entity.Image;
import com.searchimage.search_image.entity.ImageEngagement;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.entity.enums.ImageEngagementType;
import com.searchimage.search_image.repository.ImageEngagementRepository;
import com.searchimage.search_image.repository.ImageRepository;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.ImageEngagementService;
import com.searchimage.search_image.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageEngagementImpl implements ImageEngagementService {
    private JWTUtility jwtUtility;
    private ImageEngagementRepository imageEngagementRepository;
    private UserRepository userRepository;
    private ImageRepository imageRepository;
    @Autowired
    public ImageEngagementImpl(
                               JWTUtility jwtUtility,
                               ImageEngagementRepository imageEngagementRepository,
                               UserRepository userRepository,
                               ImageRepository imageRepository){
        this.jwtUtility=jwtUtility;
        this.imageEngagementRepository=imageEngagementRepository;
        this.imageRepository=imageRepository;
        this.userRepository=userRepository;
    }


    public boolean applyInteraction(Long imgId, ImageEngagementType type){


            UserPrincipal u=jwtUtility.getCurrentUser();
            Long userId=u.getUserId();

            boolean isLiked=type==ImageEngagementType.LIKE;
            boolean isSaved=type==ImageEngagementType.SAVE;
            User user=userRepository.getReferenceById(userId);
            Image image=imageRepository.getReferenceById(imgId);

            Optional<ImageEngagement> engagement=imageEngagementRepository.findByUserAndImage(user,image);



            if(engagement.isPresent()){
                ImageEngagement e=engagement.get();
                if(isLiked){
                    boolean like=e.isLiked();
                    e.setLiked(!like);
                }
                if(isSaved){
                    boolean save=e.isSaved();
                    e.setSaved(!save);
                }
                imageEngagementRepository.save(e);
            }
            else{


                ImageEngagement document=new ImageEngagement();
                document.setUser(user);
                document.setImage(image);
                document.setLiked(isLiked);
                document.setSaved(isSaved);

                imageEngagementRepository.save(document);

            }


        return true;
    }

}
