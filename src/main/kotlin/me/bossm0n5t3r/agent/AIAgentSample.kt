package me.bossm0n5t3r.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.feature.handler.agent.AgentCompletedContext
import ai.koog.agents.core.feature.handler.agent.AgentStartingContext
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

class AIAgentSample(
    openAIApiKey: String,
) {
    // Use the OpenAI executor with an API key from an environment variable
    val promptExecutor = simpleOpenAIExecutor(openAIApiKey)

    // Create a simple strategy
    val agentStrategy =
        strategy("Simple calculator") {
            // Define nodes for the strategy
            val nodeSendInput by nodeLLMRequest()
            val nodeExecuteTool by nodeExecuteTool()
            val nodeSendToolResult by nodeLLMSendToolResult()

            // Define edges between nodes
            // Start -> Send input
            edge(nodeStart forwardTo nodeSendInput)

            // Send input -> Finish
            edge(
                (nodeSendInput forwardTo nodeFinish)
                    transformed { it }
                    onAssistantMessage { true },
            )

            // Send input -> Execute tool
            edge(
                (nodeSendInput forwardTo nodeExecuteTool)
                    onToolCall { true },
            )

            // Execute tool -> Send the tool result
            edge(nodeExecuteTool forwardTo nodeSendToolResult)

            // Send the tool result -> finish
            edge(
                (nodeSendToolResult forwardTo nodeFinish)
                    transformed { it }
                    onAssistantMessage { true },
            )
        }

    // Configure the agent
    val agentConfig =
        AIAgentConfig(
            prompt =
                Prompt.build("simple-calculator") {
                    system(
                        """
                        You are a simple calculator assistant.
                        You can add two numbers together using the calculator tool.
                        When the user provides input, extract the numbers they want to add.
                        The input might be in various formats like "add 5 and 7", "5 + 7", or just "5 7".
                        Extract the two numbers and use the calculator tool to add them.
                        Always respond with a clear, friendly message showing the calculation and result.
                        """.trimIndent(),
                    )
                },
            model = OpenAIModels.Chat.GPT4o,
            maxAgentIterations = 10,
        )

    // Implement a simple calculator tool that adds two digits
    object CalculatorTool : Tool<CalculatorTool.Args, Int>(
        argsSerializer = Args.serializer(),
        resultSerializer = Int.serializer(),
        name = "calculator",
        description = "A simple calculator that can add two digits (0-9).",
    ) {
        // Arguments for the calculator tool
        @Serializable
        data class Args(
            @property:LLMDescription("The first digit to add (0-9)")
            val digit1: Int,
            @property:LLMDescription("The second digit to add (0-9)")
            val digit2: Int,
        ) {
            init {
                require(digit1 in 0..9) { "digit1 must be a single digit (0-9)" }
                require(digit2 in 0..9) { "digit2 must be a single digit (0-9)" }
            }
        }

        // Function to add two digits
        override suspend fun execute(args: Args): Int = args.digit1 + args.digit2
    }

    // Create the tool to the tool registry
    val toolRegistry =
        ToolRegistry {
            tool(CalculatorTool)
        }

    // Create the agent
    val agent =
        AIAgent(
            promptExecutor = promptExecutor,
            strategy = agentStrategy,
            agentConfig = agentConfig,
            toolRegistry = toolRegistry,
            // install the EventHandler feature
            installFeatures = {
                install(EventHandler) {
                    onAgentStarting { eventContext: AgentStartingContext ->
                        println("Starting agent: ${eventContext.agent.id}")
                    }
                    onAgentCompleted { eventContext: AgentCompletedContext ->
                        println("Result: ${eventContext.result}")
                    }
                }
            },
        )

    suspend fun main() {
        println("Enter two numbers to add (e.g., 'add 5 and 7' or '5 + 7'):")

        val userInput = readlnOrNull() ?: ""
        agent.run(userInput)
    }
}
