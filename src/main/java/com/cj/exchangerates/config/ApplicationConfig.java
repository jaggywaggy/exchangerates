package com.cj.exchangerates.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Application configuration class.
 * 
 * This class provides centralized configuration for application beans,
 * including RestTemplate, ObjectMapper, and other common components.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Configures RestTemplate for HTTP client operations.
     * 
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures ObjectMapper for JSON serialization/deserialization.
     * 
     * @return Configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Configure serialization features
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // Register JavaTimeModule for better date/time handling
        mapper.registerModule(new JavaTimeModule());
        
        return mapper;
    }
} 