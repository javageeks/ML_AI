//package com.example.ai.config;
//
//import dev.langchain4j.data.document.Document;
//import dev.langchain4j.data.embedding.Embedding;
//import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.nio.file.Files;
//
//@Component
//@RequiredArgsConstructor
//public class DocumentLoaderService {
//
//    @Qualifier("langChainEmbeddingModel")
//    private final EmbeddingModel embeddingModel;
//
//    @Qualifier("documentEmbeddingStore")
//    private final EmbeddingStore<Document> embeddingStore;
//
//    @PostConstruct
//    public void loadDocuments() {
//        try {
//            File folder = new File("src/main/resources/docs");
//            if (!folder.exists() || !folder.isDirectory()) {
//                System.out.println("No docs folder found, skipping load.");
//                return;
//            }
//
//            File[] files = folder.listFiles((dir, name) ->
//                    name.endsWith(".txt") || name.endsWith(".pdf"));
//
//            if (files == null) return;
//
//            for (File file : files) {
//                String text = extractText(file);
//                if (text.isBlank()) continue;
//
//                // Create Document
//                Document doc = Document.from(text);
//
//                // Add metadata
//                if (file.getName().toLowerCase().contains("hr")) {
//                    doc.metadata().put("category", "HR");
//                } else {
//                    doc.metadata().put("category", "General");
//                }
//                doc.metadata().put("source", file.getName());
//
//                // Generate embedding
//                Embedding embedding = embeddingModel.embed(text).content();
//
//                // Store document + embedding
//                embeddingStore.add(embedding, doc);
//
//                System.out.println("Loaded document: " + file.getName());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String extractText(File file) {
//        try {
//            if (file.getName().endsWith(".txt")) {
//                return Files.readString(file.toPath());
//            } else if (file.getName().endsWith(".pdf")) {
//                try (PDDocument pdf = PDDocument.load(file)) {
//                    return new PDFTextStripper().getText(pdf);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//}
