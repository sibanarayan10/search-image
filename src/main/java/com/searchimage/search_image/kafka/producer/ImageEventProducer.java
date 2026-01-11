package com.searchimage.search_image.kafka.producer;

import com.searchimage.search_image.entity.event.ImageUploadedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImageEventProducer {

    private final KafkaTemplate<String, ImageUploadedEvent> kafkaTemplate;

    public ImageEventProducer(KafkaTemplate<String, ImageUploadedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishImageUploaded(ImageUploadedEvent event) {
        kafkaTemplate.send("image-events", event);
    }
}
