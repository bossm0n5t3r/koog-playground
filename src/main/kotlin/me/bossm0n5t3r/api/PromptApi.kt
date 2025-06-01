package me.bossm0n5t3r.api

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.params.LLMParams

class PromptApi(
    private val openAIApiKey: String,
    private val openAILLMClient: OpenAILLMClient,
    private val googleAIStudioLLMClient: GoogleLLMClient,
) {
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

    // Execute the prompt
    suspend fun executePrompt(
        openAILLMClient: OpenAILLMClient,
        prompt: Prompt,
    ) = openAILLMClient.execute(
        prompt = prompt,
        model = OpenAIModels.Chat.GPT4o, // You can choose different models
    )

    suspend fun executePromptUsingSimpleOpenAIExecutor(prompt: Prompt): String {
        val promptExecutor = simpleOpenAIExecutor(openAIApiKey)

        // Execute a prompt
        val response =
            promptExecutor.execute(
                prompt = prompt,
                model = OpenAIModels.Chat.GPT4o,
            )

        return response
    }

    suspend fun executePrompt(
        googleLLMClient: GoogleLLMClient,
        prompt: Prompt,
    ) = googleLLMClient.execute(prompt, GoogleModels.Gemini2_0Flash001)

    suspend fun run() {
        val prompt = createPrompt()

        // OpenAI
        val openAIResponse = executePrompt(openAILLMClient, prompt)
        println("\n")
        println("=== OpenAI Response ===")
        println(openAIResponse)

        // Google AI Studio
        val googleAIResponse = executePrompt(googleAIStudioLLMClient, prompt)
        println("\n")
        println("=== Google AI Studio Response ===")
        println(googleAIResponse)

        // single provider executor
        val responseFromSingleProviderExecutor = executePromptUsingSimpleOpenAIExecutor(prompt)
        println("\n")
        println("=== Single Provider Executor Response ===")
        println(responseFromSingleProviderExecutor)
    }
}
