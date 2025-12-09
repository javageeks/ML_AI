package com.example.ai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RagServiceLc4j {

    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final EmbeddingStore<Document> embeddingStore;

    public RagServiceLc4j(
            @Qualifier("langChainEmbeddingModel") EmbeddingModel embeddingModel,
            @Qualifier("langChainChatModel") ChatModel chatModel,
            @Qualifier("documentEmbeddingStore") EmbeddingStore<Document> embeddingStore
    ) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.embeddingStore = embeddingStore;
    }

    public String answer(String question, String category) {
        try {
            // 1. Embed the question
            Embedding queryEmbedding = embeddingModel.embed(question).content();

            // 2. Optional metadata filter
            Filter filter = null;
            if (category != null && !category.isBlank() && !"All".equalsIgnoreCase(category)) {
                filter = MetadataFilterBuilder.metadataKey("category").isEqualTo(category);
            }

            // 3. Search for relevant docs
            EmbeddingSearchRequest request =
                    new EmbeddingSearchRequest(queryEmbedding, 5, 0.0, filter);

            EmbeddingSearchResult<Document> result = embeddingStore.search(request);

            // 4. Build context from matches
            String context = result.matches().stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n"));

            // 5. Build the prompt
            String prompt = """
                You are a helpful assistant. Use the following context to answer the question.

                Context:
                %s

                Question: %s

                Answer:
                """.formatted(context, question);

            // 6. Call LLM
            return chatModel.chat(prompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error in LangChain4j RAG: " + e.getMessage();
        }
    }
}
