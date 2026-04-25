package ru.gudoshnikova.troubleshootinghomework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TroubleshootingHomeworkApplication {

    public static void main(String[] args) {
        log.info("Starting CRUD Service with H2 database...");
        SpringApplication.run(TroubleshootingHomeworkApplication.class, args);
        log.info("Demo CRUD Service started successfully on port 8080");
    }
}
