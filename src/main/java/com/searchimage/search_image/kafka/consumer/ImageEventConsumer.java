//package com.searchimage.search_image.kafka.consumer;
//
//import com.searchimage.search_image.entity.Image;
//import com.searchimage.search_image.entity.enums.AiStatus;
//import com.searchimage.search_image.entity.event.ImageUploadedEvent;
//import com.searchimage.search_image.repository.ImageRepository;
//import com.searchimage.search_image.service.impl.ImageTaggingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class ImageEventConsumer {
//
//    private final ImageTaggingService taggingService;
//    private final ImageRepository imageRepository;
//
//    @Autowired
//    public ImageEventConsumer(ImageTaggingService taggingService,
//                           ImageRepository imageRepository) {
//        this.taggingService = taggingService;
//        this.imageRepository = imageRepository;
//    }
//
//    @KafkaListener(topics = "image-events")
//    public void consume(ImageUploadedEvent event) {
//
//        if (event.getImageId() == null || event.getImageUrl() == null) {
//            return; // acknowledge & skip
//        }
//
//        Optional<Image> imageOpt = imageRepository.findById(event.getImageId());
//
//        if (imageOpt.isEmpty()) {
//            return; // acknowledge & skip
//        }
//
//        Image image = imageOpt.get();
//
//        List<String> tags = taggingService.generateTags(event.getImageUrl());
//
//        image.setTags(tags);
//        image.setAiRecordStatus(AiStatus.COMPLETED);
//
//        imageRepository.save(image);
//    }
//}
//
