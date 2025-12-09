package com.example.demo_client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for discovering and invoking MCP tools via MCP clients.
 */
@Service
public class McpToolService {
    private static final Logger log = LoggerFactory.getLogger(McpToolService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, McpSyncClient> servers;

    public McpToolService(List<McpSyncClient> mcpClients) {
        this.servers = new HashMap<>();
        for (McpSyncClient client : mcpClients) {
            String serverName = client.getServerInfo().name();
            log.info("🌐 MCP server registered: {} (version={})",
                    serverName,
                    client.getServerInfo().version());
            servers.put(serverName, client);
        }
        log.info("✅ {} MCP servers initialized", servers.size());
    }

    public Map<String, List<McpTool>> getAllTools() {
        Map<String, List<McpTool>> result = new HashMap<>();

        servers.forEach((serverName, client) -> {
            SyncMcpToolCallbackProvider provider = new SyncMcpToolCallbackProvider(client);
            List<McpTool> toolInfos = new ArrayList<>();

            for (ToolCallback callback : provider.getToolCallbacks()) {
                ToolDefinition def = callback.getToolDefinition();
                toolInfos.add(new McpTool(
                        serverName,
                        def.name(),
                        def.description(),
                        def.inputSchema()
                ));
            }
            result.put(serverName, toolInfos);
        });

        logSummary(result);
        return result;
    }

    public List<McpTool> listAllTools() {
        List<McpTool> tools = new ArrayList<>();
        getAllTools().forEach((server, serverTools) -> tools.addAll(serverTools));
        log.info("📦 Total MCP tools available: {}", tools.size());
        return tools;
    }

    /**
     * Invoke an MCP tool with strict JSON validation.
     *
     * @param serverName the MCP server
     * @param toolName   the tool name
     * @param args       structured args (Map/POJO), must serialize to JSON
     */
    public String callTool(String serverName, String toolName, Object args) {
        try {
            String inputJson;

            // 🔹 Allow no-arg tools
            if (args == null) {
                inputJson = "{}";
            }
            // 🔹 If already JSON string, use directly
            else if (args instanceof String str) {
                inputJson = str;
            }
            // 🔹 Convert Map/POJO to JSON
            else {
                try {
                    inputJson = objectMapper.writeValueAsString(args);
                } catch (JsonProcessingException e) {
                    log.error("❌ Failed to serialize args for tool [{}]: {}", toolName, e.getMessage());
                    throw new IllegalArgumentException(
                            "Tool arguments must be valid JSON for tool " + toolName, e);
                }
            }

            McpSyncClient client = servers.get(serverName);
            if (client == null) {
                log.error("❌ Server not found: {}", serverName);
                throw new IllegalArgumentException("Server not found: " + serverName);
            }

            SyncMcpToolCallbackProvider provider = new SyncMcpToolCallbackProvider(client);

            for (ToolCallback callback : provider.getToolCallbacks()) {
                if (callback.getToolDefinition().name().equals(toolName)) {
                    log.info("""
                        ⚡ Executing MCP tool
                           • Server   : {}
                           • Tool     : {}
                           • Args     : {}
                        """, serverName, toolName, inputJson);

                    String result = callback.call(inputJson);

                    log.info("""
                        ✅ MCP tool result
                           • Tool     : {}
                           • Server   : {}
                           • Response : {}
                        """, toolName, serverName, result);

                    return result;
                }
            }

            log.error("❌ Tool [{}] not found on server [{}]", toolName, serverName);
            throw new IllegalArgumentException("Tool " + toolName + " not found on server " + serverName);

        } catch (Exception e) {
            log.error("❌ MCP tool invocation failed [tool={}, server={}]: {}", toolName, serverName, e.getMessage());
            throw new RuntimeException("MCP tool invocation failed: " + toolName, e);
        }
    }


    private void logSummary(Map<String, List<McpTool>> allTools) {
        log.info("📋 ===== MCP Tools Registry =====");
        allTools.forEach((server, tools) -> {
            log.info("🔌 Server: {} ({} tools)", server, tools.size());
            for (McpTool t : tools) {
                log.info("   → {} (alias='{}') : {}", t.name, t.simpleName(), t.description);
            }
        });
        log.info("================================");
    }

    public record McpTool(String server, String name, String description, String parameters) {
        public String simpleName() {
            return name.contains("_")
                    ? name.substring(name.lastIndexOf('_') + 1)
                    : name;
        }
    }
}
