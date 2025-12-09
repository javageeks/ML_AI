package com.example.ai;

//import com.example.ai.mcp.McpToolsDemoServer;

import org.springframework.ai.tool.ToolCallback;
//import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }
//    @Bean
//    public List<ToolCallback> findTools(McpToolsDemoServer mcpServer) {
//        System.out.println("------ "+ ToolCallbacks.from(mcpServer).toString());
//        List.of(ToolCallbacks.from(mcpServer)).stream().forEach(t ->
//                System.out.println("Tools on MCP Server "+t.getToolDefinition().toString())
//        );
//        return List.of(ToolCallbacks.from(mcpServer));
//    }

}
