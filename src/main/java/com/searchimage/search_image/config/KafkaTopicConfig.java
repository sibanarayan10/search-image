//package com.searchimage.search_image.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Profile("local")
//    @Bean
//    public NewTopic imageEventsTopic() {
//        return TopicBuilder
//                .name("image-events")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//}