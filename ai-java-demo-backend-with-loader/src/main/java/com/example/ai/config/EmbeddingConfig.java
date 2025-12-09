package com.example.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;

@Configuration
public class EmbeddingConfig {

    private final EmbeddingModel embeddingModel;

    public EmbeddingConfig(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return embeddingModel;
    }
}

