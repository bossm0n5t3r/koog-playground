package me.bossm0n5t3r.a2a

import ai.koog.a2a.model.Message
import ai.koog.a2a.model.MessageSendParams
import ai.koog.a2a.model.Role
import ai.koog.a2a.model.TextPart
import ai.koog.a2a.server.agent.AgentExecutor
import ai.koog.a2a.server.session.RequestContext
import ai.koog.a2a.server.session.SessionEventProcessor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class EchoAgentExecutor : AgentExecutor {
    override suspend fun execute(
        context: RequestContext<MessageSendParams>,
        eventProcessor: SessionEventProcessor,
    ) {
        val userMessage = context.params.message
        val userText =
            userMessage.parts
                .filterIsInstance<TextPart>()
                .joinToString(" ") { it.text }

        // Echo the user's message back
        val response =
            Message(
                messageId = Uuid.generateV7().toHexString(),
                role = Role.Agent,
                parts = listOf(TextPart("You said: $userText")),
                contextId = context.contextId,
                taskId = context.taskId,
            )

        eventProcessor.sendMessage(response)
    }
}
