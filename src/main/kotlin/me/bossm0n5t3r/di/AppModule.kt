package me.bossm0n5t3r.di

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.strategies.StructuredDataProcessing
import me.bossm0n5t3r.tools.AgentWithWeatherToolSet
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule =
    module {
        // Provide API keys
        single(qualifier = named("openAIApiKey")) {
            System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY environment variable is not set.")
        }

        single(qualifier = named("googleAIStudioApiKey")) {
            System.getenv("GOOGLE_AI_STUDIO_API_KEY") ?: error("GOOGLE_AI_STUDIO_API_KEY environment variable is not set.")
        }

        // Provide clients
        single {
            OpenAILLMClient(get(qualifier = named("openAIApiKey")))
        }

        single {
            GoogleLLMClient(get(qualifier = named("googleAIStudioApiKey")))
        }

        single {
            PromptApi(get(qualifier = named("openAIApiKey")), get(), get())
        }

        single { SingleApi(get(qualifier = named("openAIApiKey"))) }

        single { AIAgentSample(get(qualifier = named("openAIApiKey"))) }

        single { AgentWithWeatherToolSet(get(qualifier = named("openAIApiKey"))) }

        single { StructuredDataProcessing(get(qualifier = named("openAIApiKey"))) }
    }
