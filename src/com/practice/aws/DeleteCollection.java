package com.practice.aws;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;

public class DeleteCollection {

    public String run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return "Failed! Please provide a collection name to delete.";
        }
        
        String collectionId = args[1];
        
        DeleteCollectionRequest request = new DeleteCollectionRequest()
                .withCollectionId(collectionId);
        AmazonRekognition rekognition = ClientFactory.createClient();
        DeleteCollectionResult result = rekognition.deleteCollection(request);
        
        Integer statusCode = result.getStatusCode();
        return "Status code: " + statusCode;
    }
}
