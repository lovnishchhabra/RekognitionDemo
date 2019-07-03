package com.practice.aws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;

public class IndexFaces {

    public void run(String[] args) {

        if (args.length < 3) {
            System.err.println("Please provide a collection and images: <collection> <image-1> ... <image-n>");
            return;
        }
        
        AmazonRekognition rekognition = ClientFactory.createClient();

        String collectionId = args[1];
        for (int i = 2; i < args.length; i++) {
            
            String imageArg = args[i];
            Path path = Paths.get(imageArg);
            ByteBuffer byteBuffer;
            try {
                byte[] bytes = Files.readAllBytes(path);
                byteBuffer = ByteBuffer.wrap(bytes);
            } catch (IOException e) {
                System.err.println("Failed to read file '" + imageArg + "': " + e.getMessage());
                continue;
            }
            
            IndexFacesRequest request = new IndexFacesRequest()
                    .withCollectionId(collectionId)
                    .withDetectionAttributes("ALL")
                    .withImage(new Image().withBytes(byteBuffer))
                    .withExternalImageId(path.getFileName().toString());
            IndexFacesResult result = rekognition.indexFaces(request);
            
            System.out.println("Indexed image '" + imageArg + "':");
            
            List<FaceRecord> faceRecords = result.getFaceRecords();
            for (FaceRecord rec : faceRecords) {
                FaceDetail faceDetail = rec.getFaceDetail();
                BoundingBox bb = faceDetail.getBoundingBox();
                System.out.println("Bounding box: left=" + bb.getLeft() + 
                        "; top=" + bb.getTop() + 
                        "; width=" + bb.getWidth() + 
                        "; height=" + bb.getHeight());
                
                Face face = rec.getFace();                
                System.out.println("Face-ID: " + face.getFaceId() + 
                        "\nImage ID: " + face.getImageId() + 
                        "\nExternal Image ID: " + face.getExternalImageId() + 
                        "\nConfidence: " + face.getConfidence());
            }
        }
    }
}
