//package com.searchimage.search_image.service.impl;

//import com.google.cloud.vision.v1.*;
//import org.springframework.stereotype.Service;
//import java.util.ArrayList;
//import java.util.List;

//@Service
//public class ImageTaggingService {
//
//    private final ImageAnnotatorClient client;
//
//    public ImageTaggingService(ImageAnnotatorClient client) {
//        this.client = client;
//    }
//
//    public List<String> generateTags(String imageUrl) {
//
//        Image image = Image.newBuilder()
//                .setSource(ImageSource.newBuilder()
//                        .setImageUri(imageUrl)
//                        .build())
//                .build();
//
//        Feature feature = Feature.newBuilder()
//                .setType(Feature.Type.LABEL_DETECTION)
//                .build();
//
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder()
//                        .setImage(image)
//                        .addFeatures(feature)
//                        .build();
//
//        BatchAnnotateImagesResponse response =
//                client.batchAnnotateImages(List.of(request));
//
//        List<String> tags = new ArrayList<>();
//
//        for (AnnotateImageResponse res : response.getResponsesList()) {
//            res.getLabelAnnotationsList()
//                    .forEach(label ->
//                            tags.add(label.getDescription()));
//        }
//
//        return tags;
//    }
//}
//
