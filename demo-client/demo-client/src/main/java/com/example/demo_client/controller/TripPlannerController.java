package com.example.demo_client.controller;

import com.example.demo_client.service.TripPlannerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
    @RequestMapping("/api/mcp/agent")
public class TripPlannerController {

    private final TripPlannerService service;

    public TripPlannerController(TripPlannerService tripPlannerService) {
        this.service = tripPlannerService;
    }

    @PostMapping("/ask")
    public Map<String, String> planTrip(@RequestBody Map<String, String> request) {
        String userRequest = request.get("request");
        String response = service.planTrip(userRequest);

        // Always return structured JSON
        return Map.of("response", response);
    }
}
