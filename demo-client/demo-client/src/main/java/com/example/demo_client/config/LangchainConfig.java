package com.example.demo_client.config;

import com.example.demo_client.agent.TripPlannerAgent;
import com.example.demo_client.service.LangchainToolRegistry;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class LangchainConfig {

    private final LangchainToolRegistry toolRegistry;

    public LangchainConfig(LangchainToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }
    @Bean
    public OpenAiChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("SKO_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }


    @Bean
    public TripPlannerAgent tripPlannerAgent(OpenAiChatModel chatModel, ChatMemory chatMemory) {
        Map<ToolSpecification, ToolExecutor> tools = toolRegistry.getToolExecutors();
        return AiServices.builder(TripPlannerAgent.class)
                .chatModel(chatModel)
                .tools(tools)
                .chatMemory(chatMemory)
                .build();
    }
}
