package com.pacific.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

@RestController
@RequestMapping("/aws/api/sqs")
public class ReceiverController {

	private static final Logger log = LoggerFactory.getLogger(SQSController.class);

	@Value("${sqs.url}")
	private String sqsURL;
	
	@Value("${cloud.aws.credentials.accessKey}")
	private String awsAccessKey;
	
	@Value("${cloud.aws.credentials.secretKey}")
	private String awsSecretKey;

	@RequestMapping(value = "/messages", method=RequestMethod.GET)
	public List<Message> getMessages() {
		AWSCredentialsProvider awsCredentialsProvider =
			    new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		try {

			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withRegion(Regions.US_EAST_2)
					.withCredentials(awsCredentialsProvider).build();
			
			List<Message> result = sqs.receiveMessage(new ReceiveMessageRequest(sqsURL)).getMessages();
			
			//deleting messages after processing them
			result.forEach(message -> {
				DeleteMessageRequest deleteMessageRequest =new DeleteMessageRequest(sqsURL, message.getReceiptHandle());
				sqs.deleteMessage(deleteMessageRequest);
			});
			
			return result;
		} catch (final AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
					+ "rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			return null;
		} catch (final AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
			return null;
		}
		
	}
}
