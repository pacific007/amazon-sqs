package com.pacific.amazonsqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.pacific.controllers")
public class AmazonSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonSqsApplication.class, args);
	}

}
