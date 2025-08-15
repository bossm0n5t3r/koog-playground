package me.bossm0n5t3r.strategies

import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.tools.ToolRegistry

class PredefinedStrategiesAndCommonStrategyPatterns {
    fun singleRunStrategy(): AIAgentStrategy<String, String> =
        strategy("single_run") {
            val nodeCallLLM by nodeLLMRequest("sendInput")
            val nodeExecuteTool by nodeExecuteTool("nodeExecuteTool")
            val nodeSendToolResult by nodeLLMSendToolResult("nodeSendToolResult")

            edge(nodeStart forwardTo nodeCallLLM)
            edge(nodeCallLLM forwardTo nodeExecuteTool onToolCall { true })
            edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
            edge(nodeExecuteTool forwardTo nodeSendToolResult)
            edge(nodeSendToolResult forwardTo nodeFinish onAssistantMessage { true })
            edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCall { true })
        }

    fun toolBasedStrategy(
        name: String,
        toolRegistry: ToolRegistry,
    ): AIAgentStrategy<String, String> =
        strategy(name) {
            val nodeSendInput by nodeLLMRequest()
            val nodeExecuteTool by nodeExecuteTool()
            val nodeSendToolResult by nodeLLMSendToolResult()

            // Define the flow of the agent
            edge(nodeStart forwardTo nodeSendInput)

            // If the LLM responds with a message, finish
            edge(
                (nodeSendInput forwardTo nodeFinish)
                    onAssistantMessage { true },
            )

            // If the LLM calls a tool, execute it
            edge(
                (nodeSendInput forwardTo nodeExecuteTool)
                    onToolCall { true },
            )

            // Send the tool result back to the LLM
            edge(nodeExecuteTool forwardTo nodeSendToolResult)

            // If the LLM calls another tool, execute it
            edge(
                (nodeSendToolResult forwardTo nodeExecuteTool)
                    onToolCall { true },
            )

            // If the LLM responds with a message, finish
            edge(
                (nodeSendToolResult forwardTo nodeFinish)
                    onAssistantMessage { true },
            )
        }
}
