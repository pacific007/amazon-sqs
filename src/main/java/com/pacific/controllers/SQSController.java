package com.pacific.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueResult;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@RestController
@RequestMapping("/aws/api/sqs")
public class SQSController {
	private static final Logger log = LoggerFactory.getLogger(SQSController.class);

	@Value("${sqs.url}")
	private String sqsURL;
	
	@Value("${cloud.aws.credentials.accessKey}")
	private String awsAccessKey;
	
	@Value("${cloud.aws.credentials.secretKey}")
	private String awsSecretKey;

	@RequestMapping(value = "/createQueue", method = RequestMethod.POST)
	public String createQueue(@RequestBody String queue) {
		AWSCredentialsProvider awsCredentialsProvider =
			    new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		try {

			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withRegion(Regions.US_EAST_2)
					.withCredentials(awsCredentialsProvider).build();
			log.info("Creating " + queue + " on amazon SQS...");
			
			CreateQueueResult result = sqs.createQueue(new CreateQueueRequest(queue));
			log.info("Queue created at " + result.getQueueUrl() + " URL");
			return result.getQueueUrl();
		} catch (final AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
					+ "rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			return ase.getMessage();
		} catch (final AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
			return ace.getMessage();
		}
		
	}
	
	@RequestMapping(value = "/listQueues", method = RequestMethod.GET)
	public ListQueuesResult getQueues() {
		AWSCredentialsProvider awsCredentialsProvider =
			    new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		try {

			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withRegion(Regions.US_EAST_2)
					.withCredentials(awsCredentialsProvider).build();
			
			ListQueuesResult result = sqs.listQueues();
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
	
	@RequestMapping(value = "/deleteQueue", method = RequestMethod.POST)
	public String deleteQueue(@RequestBody String queue) {
		AWSCredentialsProvider awsCredentialsProvider =
			    new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		try {

			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withRegion(Regions.US_EAST_2)
					.withCredentials(awsCredentialsProvider).build();
			log.info("Deleting " + queue + " on amazon SQS...");
			
			DeleteQueueResult result = sqs.deleteQueue(new DeleteQueueRequest(queue));
			log.info("Queue Deleted Response : " + result.getSdkResponseMetadata() + " URL");
			return result.toString();
		} catch (final AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
					+ "rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			return ase.getMessage();
		} catch (final AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
			return ace.getMessage();
		}
		
		
	}
	
	
	@RequestMapping(value = "/sendMessageToQueue", method = RequestMethod.POST)
	public void write(@RequestBody String message) {
		AWSCredentialsProvider awsCredentialsProvider =
			    new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
		try {

			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withRegion(Regions.US_EAST_2)
					.withCredentials(awsCredentialsProvider).build();
			log.info("Sending a message to MyQueue.\n");
			
			
			sqs.sendMessage(new SendMessageRequest(sqsURL, message));
			log.info("Message Sent.\n");

		} catch (final AmazonServiceException ase) {
			log.error("Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
					+ "rejected with an error response for some reason.");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
		}
	}
}
