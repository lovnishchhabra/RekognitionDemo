package com.practice.aws;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.GetLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.GetLabelDetectionResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.LabelDetection;
import com.amazonaws.services.rekognition.model.LabelDetectionSortBy;
import com.amazonaws.services.rekognition.model.NotificationChannel;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.StartLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.StartLabelDetectionResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DetectLabelsVideo {

    private static final String SQS_QUEUE_URL = "";
    private static final String ROLE_ARN = "arn:aws:iam::AKIAWZ57HBU6VGG2B4MX";
    private static final String SNS_TOPIC_ARN = "arn:aws:iam::";

    public void run(String[] args) throws InterruptedException {

        if (args.length < 3) {
            System.err.println("Please provide a collection and images: <s3-bucket> <video>");
            return;
        }

        String s3Bucket = args[1];
        String video = args[2];

        NotificationChannel channel = new NotificationChannel().withSNSTopicArn(SNS_TOPIC_ARN).withRoleArn(ROLE_ARN);
        AmazonRekognition rekognition = ClientFactory.createClient();
        AmazonSQS sqs = ClientFactory.createSQSClient();

        String jobId = startLabelDetection(s3Bucket, video, channel, rekognition);

        System.out.println("Waiting for message with job-id:" + jobId);
        boolean jobFound = false;
        while (!jobFound) {
            List<Message> messages = sqs.receiveMessage(SQS_QUEUE_URL).getMessages();
            System.out.print(".");
            for (Message message : messages) {
                String body = message.getBody();
                try {
                    JsonNode jsonMessageTree = new ObjectMapper().readTree(body);
                    JsonNode msg = jsonMessageTree.get("Message");
                    JsonNode jsonResultTree = new ObjectMapper().readTree(msg.textValue());

                    JsonNode msgJobId = jsonResultTree.get("JobId");
                    JsonNode msgStatus = jsonResultTree.get("Status");

                    System.out.println("\nFound job: " + msgJobId);

                    if (msgJobId.asText().equals(jobId)) {
                        jobFound = true;
                        if (msgStatus.asText().equals("SUCCEEDED")) {
                            getResultsLabels(rekognition, jobId);
                        } else {
                            System.out.println("Video analysis did not succeed: " + msgStatus);
                        }
                        sqs.deleteMessage(SQS_QUEUE_URL, message.getReceiptHandle());
                    } else {
                        System.out.println("Job received was not job " + jobId + " but " + msgJobId);
                        sqs.deleteMessage(SQS_QUEUE_URL, message.getReceiptHandle());
                    }
                } catch (IOException e) {
                    System.err.println("Failed to parse message: " + e.getMessage());
                }
            }
            Thread.sleep(1000);
        }
    }

    private String startLabelDetection(String s3Bucket, String video, NotificationChannel channel,
            AmazonRekognition rekognition) {
        StartLabelDetectionRequest request = new StartLabelDetectionRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(s3Bucket)
                                .withName(video)))
                .withMinConfidence(50F)
                .withJobTag("DetectLabels")
                .withNotificationChannel(channel);

        StartLabelDetectionResult result = rekognition.startLabelDetection(request);
        System.out.println("Started label detection.");
        return result.getJobId();
    }

    private void getResultsLabels(AmazonRekognition rekognition, String jobId) {
        
        String token = null;
        do {
            
            GetLabelDetectionRequest request = new GetLabelDetectionRequest()
                    .withJobId(jobId)
                    .withMaxResults(10)
                    .withSortBy(LabelDetectionSortBy.TIMESTAMP);
            if (token != null) {
                request.withNextToken(token);
            }
            GetLabelDetectionResult result = rekognition.getLabelDetection(request);

            List<LabelDetection> labels = result.getLabels();
            for (LabelDetection detection : labels) {
                Label label = detection.getLabel();
                Long timestamp = detection.getTimestamp();
                System.out.println("Label: " + label.getName() + 
                        "; confidence=" + label.getConfidence() + 
                        "; ts=" + timestamp);
            }

            token = result.getNextToken();
        } while (token != null);
    }
}
