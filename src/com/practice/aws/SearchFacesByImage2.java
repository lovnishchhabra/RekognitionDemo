package com.practice.aws;

import java.nio.ByteBuffer;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;

public class SearchFacesByImage2 {

    public String run(String collectionId, byte[] bytes) {
    	StringBuilder res = new StringBuilder();
        ByteBuffer byteBuffer= ByteBuffer.wrap(bytes);
        SearchFacesByImageRequest request = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(new Image().withBytes(byteBuffer));
        
        AmazonRekognition rekognition = ClientFactory.createClient();
        SearchFacesByImageResult result = rekognition.searchFacesByImage(request);
        
        List<FaceMatch> faceMatches = result.getFaceMatches();
        for (FaceMatch match : faceMatches) {
            Float similarity = match.getSimilarity();
            Face face = match.getFace();
            System.out.println("MATCH:" +
                    "\nSimilarity: " + similarity + 
                    "\nFace-ID: " + face.getFaceId() + 
                    "\nImage ID: " + face.getImageId() + 
                    "\nExternal Image ID: " + face.getExternalImageId() + 
                    "\nConfidence: " + face.getConfidence());
            res.append("MATCH:" +
                    "\nSimilarity: " + similarity + 
                    "\nFace-ID: " + face.getFaceId() + 
                    "\nImage ID: " + face.getImageId() + 
                    "\nExternal Image ID: " + face.getExternalImageId() + 
                    "\nConfidence: " + face.getConfidence() + "\n");
        }
        if (res.toString().isEmpty()) {
        	res.append("Image not found in collection!");
        }
		return res.toString();
    }

}
