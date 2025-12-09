package com.example.demo_server.service;

import com.example.demo_server.model.HolidayWindow;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RagServiceLc4j {

    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final EmbeddingStore<Document> embeddingStore;

    Logger logger = LoggerFactory.getLogger(RagServiceLc4j.class);

    public RagServiceLc4j(
            @Qualifier("langChainEmbeddingModel") EmbeddingModel embeddingModel,
            @Qualifier("langChainChatModel") ChatModel chatModel,
            @Qualifier("documentEmbeddingStore") EmbeddingStore<Document> embeddingStore
    ) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.embeddingStore = embeddingStore;
    }

    // 🔹 Free-text Q&A mode (unchanged)
    public String answer(String question, String category) {
        logger.info("RagServiceLc4j: question={} category={}", question, category);

        try {
            if (question == null || question.isBlank()) {
                return "No question provided for RAG search";
            }

            Embedding queryEmbedding = embeddingModel.embed(question).content();
            Filter filter = buildFilter(category);

            EmbeddingSearchRequest request = new EmbeddingSearchRequest(queryEmbedding, 3, 0.0, filter);
            EmbeddingSearchResult<Document> result = embeddingStore.search(request);

            String context = result.matches().stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n"));

//            String prompt;
//            if ("school".equalsIgnoreCase(category) ||
//                    "office".equalsIgnoreCase(category) ||
//                    "school+office".equalsIgnoreCase(category)) {
//
//                // 🎯 Special holiday logic for free text -- still present for the LLM flows
//                prompt = """
//                You are a holiday planner assistant.
//
//                Context:
//                %s
//
//                Task:
//                - Provide only school holidays if the user asks for school holidays.
//                - Provide only office holidays if the user asks for office holidays.
//                - If both school and office holidays are present, compute the OVERLAP (intersection of date ranges).
//                - Answer with the exact overlapping holiday dates (start → end).
//                - If there is no overlap, clearly state "No common holiday window found".
//                - Never repeat the full school or office ranges without overlap calculation.
//                - Use YYYY-MM-DD format in the answer.
//
//                Question: %s
//
//                Answer:
//                """.formatted(context, question);
//            } else {
                // 🟢 Generic fallback
               String prompt = """
                You are a helpful assistant. Use the following context to answer the question.

                Context:
                %s
                Task:
                - When planning for trip find suitable locations and provide complete details of the trip.
                Question: %s

                Answer:
                """.formatted(context, question);
           // }

            logger.info("RagServiceLc4j Prompt: {}", prompt);

            return chatModel.chat(prompt);
        } catch (Exception e) {
            logger.error("Error in RAG answer: {}", e.getMessage(), e);
            return "Error in LangChain4j RAG: " + e.getMessage();
        }
    }

    // 🔹 Structured extraction mode (unchanged)
    public List<HolidayWindow> getHolidayWindows(String question, String category) {
        logger.info("Extracting structured holidays for question={} category={}", question, category);

        List<HolidayWindow> windows = new ArrayList<>();

        try {
            Embedding queryEmbedding = embeddingModel.embed(question).content();
            Filter filter = buildFilter(category);

            EmbeddingSearchRequest request = new EmbeddingSearchRequest(queryEmbedding, 5, 0.0, filter);
            EmbeddingSearchResult<Document> result = embeddingStore.search(request);

            for (var match : result.matches()) {
                String text = match.embedded().text();
                extractHolidayWindows(text, windows);
            }

        } catch (Exception e) {
            logger.error("Error extracting holidays: {}", e.getMessage(), e);
        }

        return windows;
    }

    // 🔹 New: compute deterministic overlaps between two lists of windows
    // Returns a list of HolidayWindow objects representing intersections.
    public List<HolidayWindow> computeOverlaps(List<HolidayWindow> a, List<HolidayWindow> b) {
        List<HolidayWindow> overlaps = new ArrayList<>();
        if (a == null || b == null) {
            return overlaps;
        }

        for (HolidayWindow wa : a) {
            for (HolidayWindow wb : b) {
                LocalDate start = wa.start().isAfter(wb.start()) ? wa.start() : wb.start();
                LocalDate end = wa.end().isBefore(wb.end()) ? wa.end() : wb.end();
                if (!start.isAfter(end)) {
                    overlaps.add(new HolidayWindow("overlap", start, end));
                }
            }
        }
        logger.info("Computed {} overlap windows", overlaps.size());
        return overlaps;
    }

    // 🔹 Smart filter logic
    private Filter buildFilter(String category) {
        if (category == null || category.isBlank() || "All".equalsIgnoreCase(category)) {
            // "All" → restrict only to school + office
            return MetadataFilterBuilder.metadataKey("category").isIn("school", "office");
        }
        return MetadataFilterBuilder.metadataKey("category").isEqualTo(category);
    }

    // 🔹 Extract date ranges using regex
    private void extractHolidayWindows(String text, List<HolidayWindow> windows) {
        // Example line: "Dussehra Holidays: October 1 – October 7, 2025"
        Pattern pattern = Pattern.compile("(\\w+) Holidays?:\\s*(\\w+ \\d+) ?– ?(\\w+ \\d+), (\\d{4})");
        Matcher matcher = pattern.matcher(text);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);

        while (matcher.find()) {
            String holidayType = matcher.group(1).toLowerCase();
            String startRaw = matcher.group(2) + " " + matcher.group(4);
            String endRaw = matcher.group(3) + " " + matcher.group(4);

            LocalDate start = LocalDate.parse(startRaw, fmt);
            LocalDate end = LocalDate.parse(endRaw, fmt);

            windows.add(new HolidayWindow(holidayType, start, end));
        }
    }

    // 🔹 Data record for structured response
    public record HolidayWindow(String type, LocalDate start, LocalDate end) {}
}
