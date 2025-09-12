package me.bossm0n5t3r.api

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.params.LLMParams
import me.bossm0n5t3r.me.bossm0n5t3r.utils.executeWithTitle

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

        return response.joinToString { it.content }
    }

    suspend fun executePrompt(
        googleLLMClient: GoogleLLMClient,
        prompt: Prompt,
    ) = googleLLMClient.execute(prompt, GoogleModels.Gemini2_0Flash001)

    suspend fun run() {
        val prompt = createPrompt()

        // OpenAI
        executeWithTitle("OpenAI Response") {
            val openAIResponse = executePrompt(openAILLMClient, prompt)
            println(openAIResponse)
        }

        // Google AI Studio
        executeWithTitle("Google AI Studio Response") {
            val googleAIResponse = executePrompt(googleAIStudioLLMClient, prompt)
            println(googleAIResponse)
        }

        // single provider executor
        executeWithTitle("Simple OpenAI Executor Response") {
            val responseFromSingleProviderExecutor = executePromptUsingSimpleOpenAIExecutor(prompt)
            println(responseFromSingleProviderExecutor)
        }
    }
}
