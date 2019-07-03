package com.practice.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class ClientFactory {

    public static AmazonRekognition createClient() {
	ClientConfiguration clientConfig = createClientConfiguration();
	
	AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
	
	return AmazonRekognitionClientBuilder
		.standard()
		.withClientConfiguration(clientConfig)
		.withCredentials(credentialsProvider)
		.withRegion("eu-west-1")
		.build();
    }
    
    public static AmazonSQS createSQSClient() {
        ClientConfiguration clientConfig = createClientConfiguration();
        
        return AmazonSQSClientBuilder
                .standard()
                .withClientConfiguration(clientConfig)
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion("eu-west-1")
                .build();
    }

    private static ClientConfiguration createClientConfiguration() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setConnectionTimeout(30000);
        clientConfig.setRequestTimeout(60000);
        clientConfig.setProtocol(Protocol.HTTPS);
        return clientConfig;
    }
}
