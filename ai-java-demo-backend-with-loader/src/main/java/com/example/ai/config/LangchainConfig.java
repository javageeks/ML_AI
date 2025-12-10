package com.example.ai.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangchainConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    /**
     * Embedding model (OpenAI)
     */
    @Bean(name = "langChainEmbeddingModel")
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName("text-embedding-3-small") // must match vector dimension
                .build();
    }

    /**
     * Chat model (OpenAI)
     */
    @Bean(name = "langChainChatModel")
    public ChatModel langChainChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName("gpt-4o-mini")
                .build();
    }

    /**
     * Raw PgVector store
     */
    @Bean(name = "pgVectorEmbeddingStore")
    public PgVectorEmbeddingStore pgVectorEmbeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(extractHost(jdbcUrl))
                .port(extractPort(jdbcUrl))
                .database(extractDatabase(jdbcUrl))
                .user(dbUser)
                .password(dbPass)
                .table("documents_lc4j")
                .dimension(1536)
                .build();
    }

    /**
     * Wrapper so we can work with Document instead of TextSegment
     */
    @Bean(name = "documentEmbeddingStore")
    public EmbeddingStore<Document> documentEmbeddingStore(PgVectorEmbeddingStore pgVectorStore) {
        return new DocumentEmbeddingStore(pgVectorStore);
    }

    // --- Helpers to parse JDBC ---
    private String extractHost(String url) {
        return url.split(":")[2].replace("//", "");
    }

    private int extractPort(String url) {
        return Integer.parseInt(url.split(":")[3].split("/")[0]);
    }

    private String extractDatabase(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
