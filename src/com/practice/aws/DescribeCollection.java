package com.practice.aws;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;

public class DescribeCollection {

    public String run(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide a collection name.");
            return "Failed! Please provide a collection name.";
        }
        
        DescribeCollectionRequest request = new DescribeCollectionRequest()
                .withCollectionId(args[1]);
        
        AmazonRekognition rekognition = ClientFactory.createClient();
        DescribeCollectionResult result = rekognition.describeCollection(request);
        
        System.out.println("ARN: " + result.getCollectionARN() 
            + "\nFace Model Version: " + result.getFaceModelVersion() 
            + "\nFace Count: " + result.getFaceCount()
            + "\nCreated: " + result.getCreationTimestamp());
        StringBuilder res = new StringBuilder();
        res.append("ARN: ");
        res.append(result.getCollectionARN());
        res.append("\\nFace Model Version: ");
        res.append(result.getFaceModelVersion());
        res.append("\\nFace Count: ");
        res.append(result.getFaceCount());
        res.append("\\nCreated: ");
        res.append(result.getCreationTimestamp());
        
        return res.toString();
    }
}
