package com.searchimage.search_image.service;

import com.searchimage.search_image.entity.enums.ImageEngagementType;
import org.springframework.stereotype.Service;

public interface ImageEngagementService {
    boolean applyInteraction(Long imgId, ImageEngagementType type);


}
