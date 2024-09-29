package com.authservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UberAuthServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(UberAuthServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Uber Auth Service Application...");
        SpringApplication.run(UberAuthServiceApplication.class, args);
        logger.info("Application started successfully.");
    }
}
