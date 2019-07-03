package com.practice.aws;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;

public class ListCollections {

    public String run(String[] args) {
        StringBuilder res = new StringBuilder();
        ListCollectionsRequest request = new ListCollectionsRequest()
                .withMaxResults(100);
        
        AmazonRekognition rekognition = ClientFactory.createClient();
        ListCollectionsResult result = rekognition.listCollections(request);
        
        List<String> collectionIds = result.getCollectionIds();
        while (collectionIds != null) {
            for (String id : collectionIds) {
                System.out.println(id);
                res.append(id);
                res.append("\n");
            }
            
            String token = result.getNextToken();
            if (token != null) {
                result = rekognition.listCollections(request.withNextToken(token));
            } else {
                collectionIds = null;
            }
        }
		return res.toString();
    }
}
