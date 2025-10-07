package com.crimemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan("com.crimemanagement.service")
public class ServiceConfig {
    // Configuration is now handled by component scanning and annotations
}