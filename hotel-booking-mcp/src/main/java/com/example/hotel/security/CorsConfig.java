package com.example.hotel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow SSE endpoint
                registry.addMapping("/sse")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                // Allow MCP tool communication
                registry.addMapping("/mcp/message")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
