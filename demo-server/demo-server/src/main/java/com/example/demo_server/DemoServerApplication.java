package com.example.demo_server;

import com.example.demo_server.mcp.McpToolsAgenticServer;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ai.tool.ToolCallback;
//import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

@SpringBootApplication
public class DemoServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoServerApplication.class, args);
	}

	@Bean
	public List<ToolCallback> findTools(McpToolsAgenticServer mcpServer) {
		System.out.println("------ "+ ToolCallbacks.from(mcpServer).toString());
		List.of(ToolCallbacks.from(mcpServer)).stream().forEach(t ->
				System.out.println("Tools on MCP Server "+t.getToolDefinition().toString())
		);
		return List.of(ToolCallbacks.from(mcpServer));
	}
}
