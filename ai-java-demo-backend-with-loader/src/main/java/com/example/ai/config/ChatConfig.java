package com.example.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    private final ChatClient.Builder builder;

    public ChatConfig(ChatClient.Builder builder) {
        this.builder = builder;
    }

    @Bean
    public ChatClient chatClient() {
        // Build without tool-calling support for demo
        return builder.build();
    }

}

