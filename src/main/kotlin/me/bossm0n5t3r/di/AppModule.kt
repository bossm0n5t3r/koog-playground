package me.bossm0n5t3r.di

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.mcp.GoogleMaps
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
        single {
            OpenAILLMClient(get(qualifier = named(OPENAI_API_KEY)))
        }

        single {
            GoogleLLMClient(get(qualifier = named(GOOGLE_AI_STUDIO_API_KEY)))
        }

        single {
            PromptApi(get(qualifier = named(OPENAI_API_KEY)), get(), get())
        }

        single { SingleApi(get(qualifier = named(OPENAI_API_KEY))) }

        single { AIAgentSample(get(qualifier = named(OPENAI_API_KEY))) }

        single { AgentWithWeatherToolSet(get(qualifier = named(OPENAI_API_KEY))) }

        single { StructuredDataProcessing(get(qualifier = named(OPENAI_API_KEY))) }

        single { GoogleMaps(get(qualifier = named(OPENAI_API_KEY)), get(qualifier = named(GOOGLE_MAPS_API_KEY))) }
    }
