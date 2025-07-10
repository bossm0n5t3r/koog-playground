package me.bossm0n5t3r.di

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.mcp.GoogleMaps
import me.bossm0n5t3r.mcp.Playwright
import me.bossm0n5t3r.strategies.StructuredDataProcessing
import me.bossm0n5t3r.tools.AgentWithWeatherToolSet
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val OPENAI_API_KEY = "openAIApiKey"
private const val GOOGLE_AI_STUDIO_API_KEY = "googleAIStudioApiKey"
private const val GOOGLE_MAPS_API_KEY = "googleMapsApiKey"

private const val ENVIRONMENT_VARIABLES_OPEN_API_KEY = "OPENAI_API_KEY"
private const val ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY = "GOOGLE_AI_STUDIO_API_KEY"
private const val ENVIRONMENT_VARIABLES_GOOGLE_MAPS_API_KEY = "GOOGLE_MAPS_API_KEY"

val appModule =
    module {
        // Provide API keys
        single(qualifier = named(OPENAI_API_KEY)) {
            System.getenv(ENVIRONMENT_VARIABLES_OPEN_API_KEY)
                ?: error("$ENVIRONMENT_VARIABLES_OPEN_API_KEY environment variable is not set.")
        }

        single(qualifier = named(GOOGLE_AI_STUDIO_API_KEY)) {
            System.getenv(ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY)
                ?: error("$ENVIRONMENT_VARIABLES_GOOGLE_AI_STUDIO_API_KEY environment variable is not set.")
        }

        single(qualifier = named(GOOGLE_MAPS_API_KEY)) {
            System.getenv(ENVIRONMENT_VARIABLES_GOOGLE_MAPS_API_KEY)
                ?: error("$ENVIRONMENT_VARIABLES_GOOGLE_MAPS_API_KEY environment variable is not set.")
        }

        // Provide clients
        single(createdAtStart = false) {
            OpenAILLMClient(get(qualifier = named(OPENAI_API_KEY)))
        }

        single(createdAtStart = false) {
            GoogleLLMClient(get(qualifier = named(GOOGLE_AI_STUDIO_API_KEY)))
        }

        factory {
            PromptApi(get(qualifier = named(OPENAI_API_KEY)), get(), get())
        }

        factory { SingleApi(get(qualifier = named(OPENAI_API_KEY))) }

        factory { AIAgentSample(get(qualifier = named(OPENAI_API_KEY))) }

        factory { AgentWithWeatherToolSet(get(qualifier = named(OPENAI_API_KEY))) }

        factory { StructuredDataProcessing(get(qualifier = named(OPENAI_API_KEY))) }

        factory { GoogleMaps(get(qualifier = named(OPENAI_API_KEY)), get(qualifier = named(GOOGLE_MAPS_API_KEY))) }

        factory { Playwright(get(qualifier = named(GOOGLE_AI_STUDIO_API_KEY))) }
    }
