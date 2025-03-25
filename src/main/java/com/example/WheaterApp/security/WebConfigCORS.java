package com.example.WheaterApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigCORS implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/registration")
                .allowedOrigins("http://localhost:3000")  // Ensure this is the correct frontend URL
                .allowedMethods("GET", "POST", "OPTIONS")  // Allow OPTIONS for preflight requests
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }
}

