package com.practice.aws;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;

public class DetectLabels {

    public void run(String[] args) {

        if (args.length < 2) {
            System.err.println("Please provide an image.");
            return;
        }

        String imgPath = args[1];
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(imgPath));
        } catch (IOException e) {
            System.err.println("Failed to load image: " + e.getMessage());
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        AmazonRekognition rekognition = ClientFactory.createClient();

        DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withBytes(byteBuffer)).withMaxLabels(10);
        DetectLabelsResult result = rekognition.detectLabels(request);

        List<Label> labels = result.getLabels();
        for (Label label : labels) {
            System.out.println(label.getName() + ": " + label.getConfidence());
        }
    }

}
