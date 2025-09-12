package me.bossm0n5t3r.tools

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.runBlocking

class AgentWithWeatherToolSet(
    private val openAIApiKey: String,
) {
    fun run() =
        runBlocking {
            // Create your tool set
            val weatherTools = WeatherToolSet()

            // Create an agent with your tools

            val agent =
                AIAgent(
                    executor = simpleOpenAIExecutor(openAIApiKey),
                    llmModel = OpenAIModels.Reasoning.O4Mini,
                    systemPrompt = "Provide weather information for a given location.",
                    toolRegistry =
                        ToolRegistry {
                            tools(weatherTools.asTools())
                        },
                )

            // The agent can now use your weather tools
            println(agent.run("What's the weather like in New York?"))
        }
}
