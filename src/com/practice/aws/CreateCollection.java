package com.practice.aws;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;

public class CreateCollection {

    public String run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return "Failed! Please provide a collection name.";
        }
        
        String collectionName = args[1];
        
        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(collectionName);
        
        AmazonRekognition rekognition = ClientFactory.createClient();
        CreateCollectionResult result = rekognition.createCollection(request);
        
        Integer statusCode = result.getStatusCode();
        String collectionArn = result.getCollectionArn();
        String faceModelVersion = result.getFaceModelVersion();
        StringBuilder res = new StringBuilder();
        res.append("statusCode=");
        res.append(statusCode);
        res.append("\nARN=");
        res.append(collectionArn);
        res.append("\nface model version=");
        res.append(faceModelVersion);
        System.out.println( + statusCode + "\nARN=" 
            + collectionArn + "\nface model version=" + faceModelVersion);
        return res.toString();
    }
}
