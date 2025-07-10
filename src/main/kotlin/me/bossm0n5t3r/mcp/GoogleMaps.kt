package me.bossm0n5t3r.mcp

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import me.bossm0n5t3r.LOGGER

class GoogleMaps(
    private val openAIApiToken: String,
    private val googleMapsApiKey: String,
) {
    suspend fun run() {
        // Start the Docker container with the Google Maps MCP server
        val process =
            ProcessBuilder(
                "docker",
                "run",
                "-i",
                "-e",
                "GOOGLE_MAPS_API_KEY=$googleMapsApiKey",
                "mcp/google-maps",
            ).start()

        // Create the ToolRegistry with tools from the MCP server
        val toolRegistry =
            McpToolRegistryProvider.fromTransport(
                transport = McpToolRegistryProvider.defaultStdioTransport(process),
            )

        // Create and run the agent
        val agent =
            AIAgent(
                executor = simpleOpenAIExecutor(openAIApiToken),
                llmModel = OpenAIModels.Chat.GPT4o,
                toolRegistry = toolRegistry,
            )
        LOGGER.info(agent.runAndGetResult("Get elevation of the Jetbrains Office in Munich, Germany?"))
    }
}
