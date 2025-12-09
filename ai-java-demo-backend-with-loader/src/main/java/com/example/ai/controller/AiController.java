package com.example.ai.controller;

//import com.example.ai.service.McpToolService;
//import com.example.ai.mcp.McpToolsDemoServer;
import com.example.ai.service.RagService;
//import com.example.ai.service.RagServiceLc4j;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiController {

    //private final OpenAiChatClient chatClient;
    private final RagService ragService;
    private final ChatClient chatClient;
    //private final RagServiceLc4j ragServiceLc4j;

    //private final McpToolsDemoServer mcpServer;


//    public AiController(ChatClient.Builder chatClientBuilder,RagService ragService){
//        this.chatClient = chatClientBuilder.build();
//        this.ragService = ragService;
//    }
    @GetMapping("/summarize")
    public String summarize(@RequestParam String text) {
        return chatClient.prompt()
                .user("Summarize this in 2 sentences: " + text)
                .call()
                .content();
    }

    @GetMapping("/qa")
    public String qa(@RequestParam String question) {
        return ragService.answer(question);
    }



//    @PostMapping("/qa")
//    public String qa(@RequestBody String question) {
//        return ragService.answer(question);
//    }

//    @PostMapping("/qa-lc4j") //@RequestParam("category") String category
//    public String qalc4j(@RequestBody String question, @RequestParam("category") String category) {
//        String resolvedCategory = (category != null && !category.isBlank()) ? category : "General"; // default fallback
//        return ragServiceLc4j.answer(question, resolvedCategory);
//    }


//    @PostMapping("/qa-lc4j")
//    public String qaLc4j(@RequestParam String question,
//                         @RequestParam(required = false) String category) {
//        return ragServiceLc4j.answer(question, category);
//    }

//    @GetMapping("/tool")
//    public String tool(@RequestParam String query) {
//        return mcpTools.search(query);
//    }
//    @GetMapping("/tools")
//    public List<ToolDescription> listTools() {
//        return mcpServer.listTools();
//    }
}

