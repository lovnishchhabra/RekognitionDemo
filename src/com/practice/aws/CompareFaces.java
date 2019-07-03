package com.practice.aws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;

public class CompareFaces {

    public void run(String[] args) {
        if (args.length < 3) {
            System.err.println("Please provide two images: <source-image> <target-image>.");
            return;
        }

        ByteBuffer image1 = loadImage(args[1]);        
        ByteBuffer image2 = loadImage(args[2]);
        if (image1 == null || image2 == null) {
            return;
        }
        
        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(new Image().withBytes(image1))
                .withTargetImage(new Image().withBytes(image2))
                .withSimilarityThreshold(70F);
        
        CompareFacesResult result = ClientFactory.createClient().compareFaces(request);
        
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        List<CompareFacesMatch> faceMatches = result.getFaceMatches();
        for (CompareFacesMatch match : faceMatches) {
            Float similarity = match.getSimilarity();
            System.out.println("Similarity: " + similarity);
            ComparedFace face = match.getFace();
            BoundingBox bb = face.getBoundingBox();
            boundingBoxes.add(bb);
        }
        
        BoundingBoxDrawer bbDrawer = new BoundingBoxDrawer();
        bbDrawer.drawBoundingBoxes(image2.array(), result.getTargetImageOrientationCorrection(), boundingBoxes);
    }

    private ByteBuffer loadImage(String imgPath) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(imgPath));
        } catch (IOException e) {
            System.err.println("Failed to load image: " + e.getMessage());
            return null;
        }
        return ByteBuffer.wrap(bytes);
    }

}
