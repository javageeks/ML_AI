package com.example.demo_client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LangchainToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(LangchainToolRegistry.class);
    private final McpToolService mcpToolService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LangchainToolRegistry(McpToolService mcpToolService) {
        this.mcpToolService = mcpToolService;
    }

    public Map<ToolSpecification, ToolExecutor> getToolExecutors() {
        Map<ToolSpecification, ToolExecutor> registry = new HashMap<>();

        log.info("🔧 Initializing LangChain tool registry...");

        for (McpToolService.McpTool tool : mcpToolService.listAllTools()) {
            JsonObjectSchema schema = convertSchema(tool.parameters());

            ToolSpecification spec = ToolSpecification.builder()
                    .name(tool.name())
                    .description(tool.description())
                    .parameters(schema)
                    .build();

            ToolExecutor executor = (ToolExecutionRequest request, Object ignored) -> {
                log.info("""
                        ⚡ Executing LangChain tool
                           • Server   : {}
                           • Tool     : {}
                           • Args     : {}
                        """, tool.server(), request.name(), request.arguments());

                String result = mcpToolService.callTool(tool.server(), request.name(), request.arguments());

                log.info("""
                        ✅ LangChain tool result
                           • Tool     : {}
                           • Server   : {}
                           • Response : {}
                        """, request.name(), tool.server(), result);

                return result;
            };

            registry.put(spec, executor);
        }

        logSummary(registry);

        log.info("✅ LangChain Tool Registry initialized with {} tools", registry.size());
        return registry;
    }

    /**
     * Convert MCP tool schema JSON into LangChain4j JsonObjectSchema.
     */
    private JsonObjectSchema convertSchema(String schemaJson) {
        try {
            if (schemaJson == null || schemaJson.isBlank()) {
                return new JsonObjectSchema.Builder().build();
            }
            // ✅ Deserialize directly into LangChain4j schema
            return objectMapper.readValue(schemaJson, JsonObjectSchema.class);
        } catch (Exception e) {
            log.warn("⚠️ Failed to parse tool schema, using empty schema. {}", e.getMessage());
            return new JsonObjectSchema.Builder().build();
        }
    }

    private void logSummary(Map<ToolSpecification, ToolExecutor> registry) {
        log.info("📋 ===== LangChain Tools Registry =====");
        registry.keySet().forEach(spec -> {
            String alias = spec.name().contains("_")
                    ? spec.name().substring(spec.name().lastIndexOf('_') + 1)
                    : spec.name();
            log.info("   → {} : {}", alias, spec.description());
        });
        log.info("======================================");
    }
}
