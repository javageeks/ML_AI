package com.example.demo_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class ReactiveCorsConfig {
    @Bean
    public WebFluxConfigurer reactiveCorsConfigurer() {
        return new WebFluxConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/sse")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                registry.addMapping("/mcp/message")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
