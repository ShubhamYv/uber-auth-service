package com.authservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackages = "com.entityservice.models")
public class UberAuthServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(UberAuthServiceApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Starting Uber Auth Service Application...");
		SpringApplication.run(UberAuthServiceApplication.class, args);
		LOGGER.info("Application started successfully.");
	}
}
