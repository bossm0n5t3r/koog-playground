package me.bossm0n5t3r

import ai.koog.agents.ext.agent.simpleSingleRunAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

fun main(): Unit =
    runBlocking {
        val apiKey = System.getenv("OPENAI_API_KEY") // or Anthropic, Google, OpenRouter, etc.

        val agent =
            simpleSingleRunAgent(
                executor = simpleOpenAIExecutor(apiKey), // or Anthropic, Google, OpenRouter, etc.
                systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
                llmModel = OpenAIModels.Chat.GPT4o,
            )

        val result = agent.runAndGetResult("Hello! How can you help me?")
        println(result)
    }
