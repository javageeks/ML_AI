package com.example.demo_client.service;

import com.example.demo_client.agent.TripPlannerAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * Orchestrates trip planning requests using the TripPlannerAgent (LLM + MCP tools).
 */
@Service
public class TripPlannerService {
    private static final Logger log = LoggerFactory.getLogger(TripPlannerService.class);

    private final TripPlannerAgent tripPlannerAgent;

    public TripPlannerService(TripPlannerAgent tripPlannerAgent) {
        this.tripPlannerAgent = tripPlannerAgent;
    }

    /**
     * Plan a trip based on user request.
     * Handles tool invocation errors gracefully.
     */
    public String planTrip(String userRequest) {
        log.info("📝 Received user request: {}", userRequest);

        try {
            Map<String, Object> variables = Map.of(
                    "today", LocalDate.now().toString()
            );
            String response = tripPlannerAgent.planTrip(LocalDate.now().toString(),userRequest);
            log.info("✅ TripPlannerAgent completed successfully.");
            return response;
        } catch (IllegalArgumentException e) {
            // Typically means invalid/missing JSON schema arguments
            log.warn("⚠️ Tool invocation failed due to invalid arguments: {}", e.getMessage(), e);
            return "⚠️ Sorry, I couldn’t execute your request because tool input was invalid. " +
                    "Let’s try again with clearer details.";
        } catch (RuntimeException e) {
            // General runtime errors (e.g., MCP call failures, serialization issues)
            log.error("❌ Unexpected runtime error while planning trip: {}", e.getMessage(), e);
            return "❌ Something went wrong while planning your trip. Please try again.";
        } catch (Exception e) {
            // Catch-all for other unexpected issues
            log.error("💥 Unexpected error in TripPlannerService: {}", e.getMessage(), e);
            return "💥 An unexpected error occurred. Please try again later.";
        }
    }
}
