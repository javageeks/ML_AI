//package com.example.ai.mcp;
//
//import com.example.ai.service.PromoService;
//import com.example.ai.service.RagService;
//import com.example.ai.service.RagServiceLc4j;
////import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.stereotype.Service;
//
//@Service
//public class McpToolsDemoServer {
//
//    //private final ChatClient chatClient;
//    private final ChatModel chatClient;
//    private final RagService ragService;
//    private final RagServiceLc4j ragServiceLc4j;
//    PromoService promoService;
//
//    public McpToolsDemoServer(ChatModel chatClient, RagService ragService, RagServiceLc4j ragServiceLc4j, PromoService promoService) {
//        this.chatClient = chatClient;
//        this.ragService = ragService;
//        this.ragServiceLc4j = ragServiceLc4j;
//        this.promoService = promoService;
//    }
//
//
//    @Tool(name = "summarize", description = "Summarize text into 2 sentences")
//    public String summarize(String text) {
//        return chatClient.call("Summarize this in 2 sentences: " + text);
////        return chatClient.prompt()
////                .user("Summarize this in 2 sentences: " + text)
////                .call()
////                .content();
//    }
//
//    @Tool(name = "qa", description = "Answer question using Spring AI RAG")
//    public String qa(String question) {
//        return ragService.answer(question);
//    }
//
//    @Tool(name = "qa-lc4j", description = "Answer question using LangChain4j RAG")
//    public String qaLc4j(String question, String category) {
//
//        return ragServiceLc4j.answer(question, category);
//    }
//    @Tool(name = "HR-rag", description = "Answer HR policy related questions")
//    public String hrRag(String question) {
//        return ragServiceLc4j.answer(question, "HR");
//    }
//
//    @Tool(name = "General-rag", description = "Answer general knowledge questions from internal docs")
//    public String generalRag(String question) {
//        return ragServiceLc4j.answer(question, "General");
//    }
//
//    @Tool(name = "Finance-rag", description = "Answer finance/budget related queries")
//    public String financeRag(String question) {
//        return ragServiceLc4j.answer(question, "Finance");
//    }
//
//    @Tool(name = "Promo", description = "Answer finance/budget related queries")
//    public String getPromo() {
//        return promoService.getPromo();
//    }
//}
