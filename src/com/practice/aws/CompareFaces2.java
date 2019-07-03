package com.practice.aws;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;

public class CompareFaces2 {

    public String run(byte[] image1, byte[] image2) {
        StringBuilder res = new StringBuilder();
        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(new Image().withBytes(ByteBuffer.wrap(image1)))
                .withTargetImage(new Image().withBytes(ByteBuffer.wrap(image2)))
                .withSimilarityThreshold(70F);
        
        CompareFacesResult result = ClientFactory.createClient().compareFaces(request);
        
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        List<CompareFacesMatch> faceMatches = result.getFaceMatches();
        for (CompareFacesMatch match : faceMatches) {
            Float similarity = match.getSimilarity();
            System.out.println("Similarity: " + similarity);
            res.append("Similarity: " + similarity + "\n");
            ComparedFace face = match.getFace();
            BoundingBox bb = face.getBoundingBox();
            boundingBoxes.add(bb);
        }
        
        BoundingBoxDrawer bbDrawer = new BoundingBoxDrawer();
        bbDrawer.drawBoundingBoxes(image2, result.getTargetImageOrientationCorrection(), boundingBoxes);
        if (res.toString().isEmpty()) {
        	res.append("No similarity found!");
        }
		return res.toString();
    }

}
