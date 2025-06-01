package me.bossm0n5t3r.api

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.agent.simpleSingleRunAgent
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import me.bossm0n5t3r.me.bossm0n5t3r.utils.executeWithTitle

class SingleApi(
    private val openAIApiKey: String,
) {
    suspend fun run() {
        executeWithTitle("Create a single-run agent and run it") {
            createSingleRunAgentAndRun()
        }

        executeWithTitle("Configure an agent using tools") {
            configureAgentUsingTools()
        }
    }

    suspend fun createSingleRunAgentAndRun() {
        val agent =
            simpleSingleRunAgent(
                executor = simpleOpenAIExecutor(openAIApiKey),
                systemPrompt = "You are a code assistant. Provide concise code examples.",
                llmModel = OpenAIModels.Chat.GPT4o,
            )

        agent.run("Write a Kotlin function to calculate factorial")
    }

    suspend fun configureAgentUsingTools() {
        val toolRegistry =
            ToolRegistry {
                tools(
                    listOf(SayToUser),
                )
            }

        val agent =
            simpleSingleRunAgent(
                executor = simpleOpenAIExecutor(openAIApiKey),
                toolRegistry = toolRegistry,
                systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
                llmModel = OpenAIModels.Chat.GPT4o,
            )
        agent.run("Hello, how can you help me?")
    }
}
