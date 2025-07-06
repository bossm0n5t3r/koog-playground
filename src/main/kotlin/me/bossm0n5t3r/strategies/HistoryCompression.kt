package me.bossm0n5t3r.strategies

import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.core.agent.session.AIAgentLLMWriteSession
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.HistoryCompressionStrategy
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.memory.feature.history.RetrieveFactsFromHistory
import ai.koog.agents.memory.model.Concept
import ai.koog.agents.memory.model.FactType
import ai.koog.prompt.message.Message

object HistoryCompression {
    object HistoryCompressionInAStrategyGraph {
        // Define that the history is too long if there are more than 100 messages
        private suspend fun AIAgentContextBase.historyIsTooLong(): Boolean = llm.readSession { prompt.messages.size > 100 }

        val strategy =
            strategy("execute-with-history-compression") {
                val callLLM by nodeLLMRequest()
                val executeTool by nodeExecuteTool()
                val sendToolResult by nodeLLMSendToolResult()

                // Compress the LLM history and keep the current ReceivedToolResult for the next node
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>()

                edge(nodeStart forwardTo callLLM)
                edge(callLLM forwardTo nodeFinish onAssistantMessage { true })
                edge(callLLM forwardTo executeTool onToolCall { true })

                // Compress history after executing any tool if the history is too long
                edge(executeTool forwardTo compressHistory onCondition { historyIsTooLong() })
                edge(compressHistory forwardTo sendToolResult)
                // Otherwise, proceed to the next LLM request
                edge(executeTool forwardTo sendToolResult onCondition { !historyIsTooLong() })

                edge(sendToolResult forwardTo executeTool onToolCall { true })
                edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
            }
    }

    object HistoryCompressionStrategies {
        val wholeHistoryStrategy =
            strategy("whole-history-strategy") {
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
                    strategy = HistoryCompressionStrategy.WholeHistory,
                )
            }

        val memoryPreservationDuringCompressionStrategy =
            strategy("memory-preservation-during-compression") {
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
                    strategy = HistoryCompressionStrategy.WholeHistory,
                    preserveMemory = true,
                )
            }

        val fromLastNMessagesStrategy =
            strategy("from-last-n-messages") {
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
                    strategy = HistoryCompressionStrategy.FromLastNMessages(100),
                )
            }

        val chunkedStrategy =
            strategy("chunked-strategy") {
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
                    strategy = HistoryCompressionStrategy.Chunked(100),
                )
            }

        val retrieveFactsFromHistoryStrategy =
            strategy("retrieve-facts-from-history") {
                val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
                    strategy =
                        RetrieveFactsFromHistory(
                            Concept(
                                keyword = "user_preferences",
                                // Description to the LLM -- what specifically to search for
                                description =
                                    "User's preferences for the recommendation system, " +
                                        "including the preferred conversation style, theme in the application, etc.",
                                // LLM would search for multiple relevant facts related to this concept:
                                factType = FactType.MULTIPLE,
                            ),
                            Concept(
                                keyword = "product_details",
                                // Description to the LLM -- what specifically to search for
                                description = "Brief details about products in the catalog the user has been checking",
                                // LLM would search for multiple relevant facts related to this concept:
                                factType = FactType.MULTIPLE,
                            ),
                            Concept(
                                keyword = "issue_solved",
                                // Description to the LLM -- what specifically to search for
                                description = "Was the initial user's issue resolved?",
                                // LLM would search for a single answer to the question:
                                factType = FactType.SINGLE,
                            ),
                        ),
                )
            }
    }

    class MyCustomCompressionStrategy : HistoryCompressionStrategy() {
        override suspend fun compress(
            llmSession: AIAgentLLMWriteSession,
            preserveMemory: Boolean,
            memoryMessages: List<Message>,
        ) {
            // 1. Process the current history in llmSession.prompt.messages
            // 2. Create new compressed messages
            // 3. Update the prompt with the compressed messages

            // Example implementation:
            val importantMessages =
                llmSession.prompt.messages
                    .filter {
                        // Your custom filtering logic
                        it.content.contains("important")
                    }.filterIsInstance<Message.Response>()

            // Note: you can also make LLM requests using the `llmSession` and ask the LLM to do some job for you using, for example, `llmSession.requestLLMWithoutTools()`
            // Or you can change the current model: `llmSession.model = AnthropicModels.Sonnet_3_7` and ask some other LLM model -- but don't forget to change it back after

            // Compose the prompt with the filtered messages
            composePromptWithRequiredMessages(
                llmSession,
                importantMessages,
                preserveMemory,
                memoryMessages,
            )
        }
    }
}
