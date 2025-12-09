package com.example.demo_server.mcp;

import com.example.demo_server.service.RagServiceLc4j;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP tools exposed by the demo-server.
 */
@Component
public class McpToolsAgenticServer {

    private final RagServiceLc4j ragServiceLc4j;

    public McpToolsAgenticServer(RagServiceLc4j ragServiceLc4j) {
        this.ragServiceLc4j = ragServiceLc4j;
    }

    /**
     * School holiday planner tool.
     */
//    @Tool(
//            name = "schoolHolidayPlanner",
//            description = "Get holidays related to schools (vacations, breaks, etc.)"
//    )
//    public String schoolHolidayPlanner(@JsonProperty("question") String question) {
//        return ragServiceLc4j.answer(question, "school");
//    }
//
//    /**
//     * Office holiday planner tool.
//     */
//    @Tool(
//            name = "officeHolidayPlanner",
//            description = "Get holidays related to offices (public and company holidays)"
//    )
//    public String officeHolidayPlanner(@JsonProperty("question") String question) {
//        return ragServiceLc4j.answer(question, "office");
//    }

    /**
     * Family holiday planner tool (intersection of school + office holidays).
     */
    @Tool(
            name = "HolidayPlanner",
            description = "Get overlapping holiday windows suitable for family trips (school + office overlap)"
    )
    public String familyHolidayPlanner(@JsonProperty("question") String question) {
        return ragServiceLc4j.answer(question, "holidays");
    }

    /**
     * Structured extractor for holiday windows.
     * Can be used by client for precise date range extraction if needed.
     */
//    @Tool(
//            name = "holidayWindows",
//            description = "Get structured holiday windows (start and end dates) for schools or offices"
//    )
//    public List<RagServiceLc4j.HolidayWindow> holidayWindows(HolidayWindowRequest request) {
//        return ragServiceLc4j.getHolidayWindows(request.question, "all");
//    }

    public static class HolidayWindowRequest {
        @JsonProperty("question")
        public String question;
    }


//    @Tool(
//            name = "holidayWindows",
//            description = "Get structured holiday windows (start and end dates) for schools/offices"
//    )
//    public List<RagServiceLc4j.HolidayWindow> holidayWindows(HolidayWindowRequest request) {
//        return ragServiceLc4j.getHolidayWindows(request.question, "holidays");
//    }

    /**
     * (Optional) Enable this if you want multiple domain-specific RAG tools.
     */
//    @Tool(name = "hrPolicyRag", description = "Answer HR policy related questions")
//    public String hrRag(String question) {
//        return ragServiceLc4j.answer(question, "HR");
//    }
//
//    @Tool(name = "generalKnowledgeRag", description = "Answer general knowledge questions from internal docs")
//    public String generalRag(String question) {
//        return ragServiceLc4j.answer(question, "General");
//    }
//
//    @Tool(name = "financeRag", description = "Answer finance/budget related queries")
//    public String financeRag(String question) {
//        return ragServiceLc4j.answer(question, "Finance");
//    }
}
