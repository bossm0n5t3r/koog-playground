import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

fun main(): Unit =
    runBlocking {
        val apiKey =
            System.getenv("OPENAI_API_KEY") // or Anthropic, Google, OpenRouter, etc.
                ?: error("The API key is not set.")

        val agent =
            AIAgent(
                promptExecutor = simpleOpenAIExecutor(apiKey), // or Anthropic, Google, OpenRouter, etc.
                systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
                llmModel = OpenAIModels.Chat.GPT4o,
            )

        val result = agent.run("Hello! How can you help me?")
        println(result)
    }
