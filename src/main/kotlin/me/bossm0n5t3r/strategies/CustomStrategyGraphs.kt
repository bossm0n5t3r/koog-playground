package me.bossm0n5t3r.strategies

import ai.koog.agents.core.agent.entity.AIAgentStrategy
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.tools.ToolRegistry

class CustomStrategyGraphs {
    val myStrategy =
        strategy("my-strategy") {
            val nodeCallLLM by nodeLLMRequest()
            val executeToolCall by nodeExecuteTool()
            val sendToolResult by nodeLLMSendToolResult()

            edge(nodeStart forwardTo nodeCallLLM)
            edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
            edge(nodeCallLLM forwardTo executeToolCall onToolCall { true })
            edge(executeToolCall forwardTo sendToolResult)
            edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
            edge(sendToolResult forwardTo executeToolCall onToolCall { true })
        }

    fun toneStrategy(
        name: String,
        toolRegistry: ToolRegistry,
    ): AIAgentStrategy =
        strategy(name) {
            val nodeSendInput by nodeLLMRequest()
            val nodeExecuteTool by nodeExecuteTool()
            val nodeSendToolResult by nodeLLMSendToolResult()
            val nodeCompressHistory by nodeLLMCompressHistory<ReceivedToolResult>()

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

            // If the history gets too large, compress it
            edge(
                (nodeExecuteTool forwardTo nodeCompressHistory)
                    onCondition { _ -> llm.readSession { prompt.messages.size > 100 } },
            )

            edge(nodeCompressHistory forwardTo nodeSendToolResult)

            // Otherwise, send the tool result directly
            edge(
                (nodeExecuteTool forwardTo nodeSendToolResult)
                    onCondition { _ -> llm.readSession { prompt.messages.size <= 100 } },
            )

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
