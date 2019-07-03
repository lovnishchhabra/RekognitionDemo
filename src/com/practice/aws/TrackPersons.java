package com.practice.aws;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Gender;
import com.amazonaws.services.rekognition.model.GetPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.GetPersonTrackingResult;
import com.amazonaws.services.rekognition.model.NotificationChannel;
import com.amazonaws.services.rekognition.model.PersonDetail;
import com.amazonaws.services.rekognition.model.PersonDetection;
import com.amazonaws.services.rekognition.model.PersonTrackingSortBy;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.StartPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.StartPersonTrackingResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrackPersons {

    private static final String SQS_QUEUE_URL = "";
    private static final String ROLE_ARN = "";
    private static final String SNS_TOPIC_ARN = "";

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

        String jobId = startPersonTracking(s3Bucket, video, channel, rekognition);

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
                            getPersonTracking(rekognition, jobId);
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

    private void getPersonTracking(AmazonRekognition rekognition, String jobId) {
        String token = null;
        do {
            GetPersonTrackingRequest request = new GetPersonTrackingRequest()
                    .withJobId(jobId)
                    .withMaxResults(10)
                    .withSortBy(PersonTrackingSortBy.TIMESTAMP);
            if (token != null) {
                request.setNextToken(token);
            }
            GetPersonTrackingResult result = rekognition.getPersonTracking(request);
            List<PersonDetection> persons = result.getPersons();
            for (PersonDetection detection : persons) {
                PersonDetail person = detection.getPerson();
                Long index = person.getIndex();
                Long timestamp = detection.getTimestamp();
                System.out.println("Face with id " + index + " detected at " + timestamp + ".");
            }
            token = result.getNextToken();
        } while (token != null);
    }

    private String startPersonTracking(String s3Bucket, String video, NotificationChannel channel,
            AmazonRekognition rekognition) {
        
        StartPersonTrackingRequest request = new StartPersonTrackingRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(s3Bucket)
                                .withName(video)))
                .withJobTag("track-person")
                .withNotificationChannel(channel);
        StartPersonTrackingResult result = rekognition.startPersonTracking(request);
        return result.getJobId();
    }

}
