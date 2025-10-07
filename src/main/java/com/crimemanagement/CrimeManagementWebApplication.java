package com.crimemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrimeManagementWebApplication {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CrimeManagementWebApplication.class);
        app.setRegisterShutdownHook(true);
        app.run(args);
    }
}
