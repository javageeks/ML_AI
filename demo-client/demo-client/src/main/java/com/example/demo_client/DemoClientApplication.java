package com.example.demo_client;

//import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import jakarta.annotation.PostConstruct;
@SpringBootApplication
public class DemoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoClientApplication.class, args);
//		System.out.println("-------"+ McpToolUtils.toSyncToolSpecification(toolCallbacks));
//		McpToolUtils.getToolCallbacksFromSyncClients((McpSyncClient) toolCallbacks).stream().forEach(tool ->{
//			System.out.println("Tool: " + tool.getToolDefinition().name() +
//					" - " + tool.getToolDefinition().description());
//		});
	}
	


}
