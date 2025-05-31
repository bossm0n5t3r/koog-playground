package me.bossm0n5t3r.api

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.params.LLMParams

class PromptApi {
    fun createPrompt(): Prompt =
        prompt("prompt_name", LLMParams()) {
            // Add a system message to set the context
            system("You are a helpful assistant.")

            // Add a user message
            user("Tell me about Kotlin")

            // You can also add assistant messages for few-shot examples
            assistant("Kotlin is a modern programming language...")

            // Add another user message
            user("What are its key features?")
        }

    // Create an OpenAI client
    val openAILLMClient: OpenAILLMClient by lazy {
        val apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY environment variable is not set.")
        OpenAILLMClient(apiKey)
    }

    // Execute the prompt
    suspend fun executePrompt(
        openAILLMClient: OpenAILLMClient,
        prompt: Prompt,
    ) = openAILLMClient.execute(
        prompt = prompt,
        model = OpenAIModels.Chat.GPT4o, // You can choose different models
    )

    suspend fun executePromptUsingSimpleOpenAIExecutor(prompt: Prompt): String {
        val apiKey = System.getenv("OPENAI_API_KEY") ?: error("OPENAI_API_KEY environment variable is not set.")
        val promptExecutor = simpleOpenAIExecutor(apiKey)

        // Execute a prompt
        val response =
            promptExecutor.execute(
                prompt = prompt,
                model = OpenAIModels.Chat.GPT4o,
            )

        return response
    }

    // Create a Google client
    val googleAIStudioLLMClient: GoogleLLMClient by lazy {
        val apiKey = System.getenv("GOOGLE_AI_STUDIO_API_KEY") ?: error("GOOGLE_AI_STUDIO_API_KEY environment variable is not set.")
        GoogleLLMClient(apiKey)
    }

    suspend fun executePrompt(
        googleLLMClient: GoogleLLMClient,
        prompt: Prompt,
    ) = googleLLMClient.execute(prompt, GoogleModels.Gemini2_0Flash001)

    suspend fun run() {
        val promptApi = PromptApi()

        val prompt = promptApi.createPrompt()

        // OpenAI
        val openAIResponse = promptApi.executePrompt(promptApi.openAILLMClient, prompt)
        println(openAIResponse)

        // Google AI Studio
        val googleAIResponse = promptApi.executePrompt(promptApi.googleAIStudioLLMClient, prompt)
        println(googleAIResponse)
    }
}
