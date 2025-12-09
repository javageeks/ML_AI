package com.example.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.Embedding;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple RAG service:
 * - Computes embeddings via Spring AI's embedding (placeholder)
 * - Queries PostgreSQL+pgvector table for nearest neighbors using "<->" or "<=>"
 * - Calls ChatClient to synthesize answer using retrieved contexts.
 *
 * Replace getQueryEmbedding with actual embeddings call (e.g., Spring AI embeddings client).
 */
@Service
public class RagService {

    private final ChatClient chatClient;
    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPass;
    private final EmbeddingModel embeddingModel;
    public RagService(ChatClient chatClient, EmbeddingModel embeddingModel,
                      @Value("${spring.datasource.url}") String jdbcUrl,
                      @Value("${spring.datasource.username}") String dbUser,
                      @Value("${spring.datasource.password}") String dbPass) {
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
        this.jdbcUrl = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public String answer(String question) {
        try {
            double[] qEmbedding = getQueryEmbedding(question);
            List<String> contexts = queryNearestNeighbors(qEmbedding, 3);

            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a helpful assistant. Use the following context documents to answer:\n");
            for (String ctx : contexts) {
                prompt.append("- ").append(ctx).append("\n");
            }
            prompt.append("\nQuestion: ").append(question).append("\nAnswer:");

            return chatClient.prompt()
                    .user(prompt.toString())
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during RAG: " + e.getMessage();
        }
    }

    private double[] getQueryEmbedding(String text) {
        // TODO: Replace with real call to embeddings API and return embedding vector.
        // Build request with your input text
        // Call Spring AI embeddings
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));

        // Get the first embedding as float[]
        float[] vector = response.getResults().get(0).getOutput();

        // Convert float[] → double[] for consistency
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i];
        }
        return result;
        //return new double[1536];
    }

    private List<String> queryNearestNeighbors(double[] embedding, int k) throws SQLException {
        List<String> results = new ArrayList<>();

        String sql = "SELECT content " +
                "FROM documents " +
                "ORDER BY embedding <-> ?::vector " +
                "LIMIT ?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Convert primitive double[] → boxed Double[]
            Double[] boxed = new Double[embedding.length];
            for (int i = 0; i < embedding.length; i++) {
                boxed[i] = embedding[i];
            }

            // Create SQL array of type float8 (Postgres double precision)
            Array sqlArray = conn.createArrayOf("float8", boxed);

            // Bind array and limit
            ps.setArray(1, sqlArray);
            ps.setInt(2, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("content"));
                }
            }
        }
        return results;
    }

}
