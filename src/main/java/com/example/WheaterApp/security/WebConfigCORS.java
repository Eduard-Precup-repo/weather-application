package com.example.WheaterApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigCORS {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Apply CORS to all endpoints
                        .allowedOrigins("http://localhost:5173")  // React frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow all required HTTP methods
                        .allowedHeaders("*")  // Allow all headers
                        .allowCredentials(true);
            }
        };
    }
}
