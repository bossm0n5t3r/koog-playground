package me.bossm0n5t3r.mcp

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import kotlinx.coroutines.delay
import me.bossm0n5t3r.LOGGER
import java.io.IOException
import java.net.ConnectException

/**
 * A class that provides browser automation capabilities using Playwright MCP (Model-Control-Protocol).
 * This class handles starting the Playwright MCP server, connecting to it, and running browser automation tasks.
 *
 * @property googleAIStudioApiKey The API key for Google AI Studio, used for the LLM executor.
 */
class Playwright(
    private val googleAIStudioApiKey: String,
) {
    companion object {
        private const val DEFAULT_MCP_PORT = 8931
        private const val DEFAULT_CONNECTION_RETRIES = 10
        private const val DEFAULT_RETRY_DELAY_MS = 2000L
    }

    /**
     * Runs a browser automation task using Playwright MCP.
     * This method starts the Playwright MCP server, connects to it, and executes a predefined task.
     */
    suspend fun run() {
        // Start the Playwright MCP server
        val process = startPlaywrightMcpServer()

        try {
            // Create the ToolRegistry with tools from the MCP server, with retries
            val serverUrl = "http://localhost:$DEFAULT_MCP_PORT"
            val toolRegistry = createToolRegistryWithRetries(serverUrl)

            // Execute the browser automation task
            executeBrowserTask(toolRegistry)
        } catch (e: Exception) {
            // Ensure the process is terminated in case of errors
            process.destroy()
            throw e
        }
    }

    /**
     * Starts the Playwright MCP server on the specified port.
     *
     * @param port The port on which to start the MCP server.
     * @return The Process object representing the running MCP server.
     * @throws IOException If the process fails to start.
     */
    private fun startPlaywrightMcpServer(port: Int = DEFAULT_MCP_PORT): Process {
        LOGGER.info("Starting Playwright MCP server on port $port...")
        return ProcessBuilder(
            "npx",
            "@playwright/mcp@latest",
            "--port",
            port.toString(),
        ).start().also {
            LOGGER.info("Playwright MCP server process started")
        }
    }

    /**
     * Executes a browser automation task using the provided tool registry.
     *
     * @param toolRegistry The tool registry containing the Playwright MCP tools.
     */
    private suspend fun executeBrowserTask(toolRegistry: ToolRegistry) {
        LOGGER.info("Creating AI agent for browser automation...")
        val agent =
            AIAgent(
                promptExecutor = simpleGoogleAIExecutor(googleAIStudioApiKey),
                llmModel = GoogleModels.Gemini2_0Flash,
                toolRegistry = toolRegistry,
            )

        LOGGER.info("Executing browser automation task...")
        agent.run("Open a browser, navigate to jetbrains.com, accept all cookies, click AI in toolbar")
        LOGGER.info("Browser automation task completed")
    }

    /**
     * Creates a McpToolRegistryProvider with retry logic to handle server startup delays.
     *
     * @param url The URL of the MCP server.
     * @param retries The maximum number of connection attempts.
     * @param delayMillis The delay between retries in milliseconds.
     * @return An instance of ToolRegistry.
     * @throws IllegalStateException if the connection fails after all, retries.
     */
    private suspend fun createToolRegistryWithRetries(
        url: String,
        retries: Int = DEFAULT_CONNECTION_RETRIES,
        delayMillis: Long = DEFAULT_RETRY_DELAY_MS,
    ): ToolRegistry {
        LOGGER.info("Attempting to connect to Playwright MCP server at $url...")
        var lastException: Exception? = null

        for (attempt in 1..retries) {
            try {
                val transport = McpToolRegistryProvider.defaultSseTransport(url)
                val toolRegistry = McpToolRegistryProvider.fromTransport(transport)
                LOGGER.info("Successfully connected to Playwright MCP server")
                return toolRegistry
            } catch (e: Exception) {
                if (isConnectException(e)) {
                    lastException = e
                    LOGGER.warn("Connection to Playwright MCP server failed (attempt $attempt/$retries). Retrying in ${delayMillis}ms...")
                    delay(delayMillis)
                } else {
                    LOGGER.error("Unexpected error while connecting to Playwright MCP server: ${e.message}")
                    throw e // Re-throw exceptions that are not related to connection refusal
                }
            }
        }

        throw IllegalStateException(
            "Failed to connect to Playwright MCP server at $url after $retries attempts.",
            lastException,
        )
    }

    /**
     * Checks if the given exception or contains a ConnectException.
     *
     * @param exception The exception to check.
     * @return True if the exception or contains a ConnectException, false otherwise.
     */
    private fun isConnectException(exception: Exception): Boolean {
        var cause: Throwable? = exception
        while (cause != null) {
            if (cause is ConnectException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }
}
