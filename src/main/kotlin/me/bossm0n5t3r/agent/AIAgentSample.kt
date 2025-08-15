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
import ai.koog.agents.core.feature.handler.AgentFinishedContext
import ai.koog.agents.core.feature.handler.AgentStartContext
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.ToolResult
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.serialization.Serializable

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

    // Implement s simple calculator tool that can add two numbers
    object CalculatorTool : Tool<CalculatorTool.Args, ToolResult>() {
        @Serializable
        data class Args(
            val num1: Int,
            val num2: Int,
        ) : ToolArgs

        @Serializable
        data class Result(
            val sum: Int,
        ) : ToolResult {
            override fun toStringDefault(): String = "The sum is: $sum"
        }

        override val argsSerializer = Args.serializer()

        override val descriptor =
            ToolDescriptor(
                name = "calculator",
                description = "Add two numbers together",
                requiredParameters =
                    listOf(
                        ToolParameterDescriptor(
                            name = "num1",
                            description = "First number to add",
                            type = ToolParameterType.Integer,
                        ),
                        ToolParameterDescriptor(
                            name = "num2",
                            description = "Second number to add",
                            type = ToolParameterType.Integer,
                        ),
                    ),
            )

        override suspend fun execute(args: Args): Result {
            // Perform a simple addition operation
            val sum = args.num1 + args.num2
            return Result(sum)
        }
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
                    onBeforeAgentStarted { eventContext: AgentStartContext<*> ->
                        println("Starting strategy: ${eventContext.strategy.name}")
                    }
                    onAgentFinished { eventContext: AgentFinishedContext ->
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
