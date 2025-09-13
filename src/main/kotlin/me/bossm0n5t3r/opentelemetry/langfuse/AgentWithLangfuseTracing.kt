package me.bossm0n5t3r.opentelemetry.langfuse

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.agents.features.opentelemetry.integration.langfuse.addLangfuseExporter
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import kotlinx.coroutines.runBlocking
import me.bossm0n5t3r.di.ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY
import java.io.File

fun main() =
    runBlocking {
        val apiKey =
            System.getenv(ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY)
                ?: error("$ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY environment variable is not set.")

        val langfuseProperties = getLangfuseInitialPropertiesFromFile()

        val agent =
            AIAgent(
                executor = simpleGoogleAIExecutor(apiKey),
                llmModel = GoogleModels.Gemini2_5Flash,
                systemPrompt = "You are a code assistant. Provide concise code examples.",
            ) {
                install(OpenTelemetry) {
                    addLangfuseExporter(
                        langfuseUrl = langfuseProperties.getOrThrow(LANGFUSE_URL),
                        langfusePublicKey = langfuseProperties.getOrThrow(LANGFUSE_INITIAL_PROJECT_PUBLIC_KEY),
                        langfuseSecretKey = langfuseProperties.getOrThrow(LANGFUSE_INITIAL_PROJECT_SECRET_KEY),
                    )
                }
            }

        println("Running agent with Langfuse tracing")

        val result = agent.run("Tell me a joke about programming")

        println("Result: $result\nSee traces on the Langfuse instance")
    }

private const val LANGFUSE_INITIAL_PROPERTIES_FILE = "src/main/kotlin/me/bossm0n5t3r/opentelemetry/langfuse/.env"
private const val LANGFUSE_URL = "LANGFUSE_URL"
private const val LANGFUSE_INITIAL_PROJECT_PUBLIC_KEY = "LANGFUSE_INIT_PROJECT_PUBLIC_KEY"
private const val LANGFUSE_INITIAL_PROJECT_SECRET_KEY = "LANGFUSE_INIT_PROJECT_SECRET_KEY"

private fun getLangfuseInitialPropertiesFromFile(): Map<String, String> {
    val envFile = File(LANGFUSE_INITIAL_PROPERTIES_FILE)
    if (!envFile.exists()) {
        error("Langfuse properties file not found at: ${envFile.absolutePath}")
    }
    return envFile
        .readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .mapNotNull { line ->
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) {
                parts[0].trim() to parts[1].trim()
            } else {
                null
            }
        }.associate { it } + (LANGFUSE_URL to "http://localhost:3000")
}

private fun Map<String, String>.getOrThrow(key: String): String = this[key] ?: error("'$key' not found in the .env file.")
