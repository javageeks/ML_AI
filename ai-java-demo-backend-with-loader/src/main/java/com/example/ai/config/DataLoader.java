package com.example.ai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

//import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class DataLoader {

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;
    public DataLoader(EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
    }

    //@PostConstruct
    public void load() throws IOException {
        Path docsPath = Paths.get("src/main/resources/docs");
        if (!Files.exists(docsPath)) {
            System.out.println("No docs found.");
            return;
        }

        Files.list(docsPath).forEach(file -> {
            try {
                String content = Files.readString(file);

                // NOTE: embed takes a List<String> and returns List<float[]>
                List<float[]> embeddings = embeddingModel.embed(List.of(content));
                float[] vector = embeddings.get(0); // first embedding

                // Build pgvector literal: [0.123,0.456,...]
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                for (int i = 0; i < vector.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(vector[i]);
                }
                sb.append(']');
                String vectorString = sb.toString();

                String sql = "INSERT INTO documents (content, embedding) VALUES (?, ?)";
                // Use JdbcTemplate.update with PreparedStatementCreator so we can set a PGobject
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1, content);
                    PGobject pgVector = new PGobject();
                    pgVector.setType("vector");
                    try {
                        pgVector.setValue(vectorString); // can throw SQLException
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ps.setObject(2, pgVector);
                    return ps;
                });

                System.out.println("Inserted: " + file.getFileName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
