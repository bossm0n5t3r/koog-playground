package me.bossm0n5t3r.a2a

import ai.koog.a2a.client.A2AClient
import ai.koog.a2a.client.UrlAgentCardResolver
import ai.koog.a2a.model.Message
import ai.koog.a2a.model.MessageSendParams
import ai.koog.a2a.model.Role
import ai.koog.a2a.model.TaskEvent
import ai.koog.a2a.model.TextPart
import ai.koog.a2a.transport.Request
import ai.koog.a2a.transport.client.jsonrpc.http.HttpJSONRPCClientTransport
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SimpleA2AClient {
    // HTTP JSON-RPC transport
    val transport =
        HttpJSONRPCClientTransport(
            url = "http://localhost:$PORT$ECHO_AGENT_PATH",
        )

    // Agent card resolver
    val agentCardResolver =
        UrlAgentCardResolver(
            baseUrl = "http://localhost:$PORT",
            path = ECHO_AGENT_CARD_PATH,
        )

    // Create client
    val client = A2AClient(transport, agentCardResolver)
}

@OptIn(ExperimentalUuidApi::class)
suspend fun main() {
    val client = SimpleA2AClient().client
    // Connect and retrieve agent capabilities
    client.connect()
    val agentCard = client.cachedAgentCard()

    println("Connected to: ${agentCard.name}")
    println("Supports streaming: ${agentCard.capabilities.streaming}")

    val message =
        Message(
            messageId = Uuid.generateV7().toHexString(),
            role = Role.User,
            parts = listOf(TextPart("Hello, agent!")),
            contextId = "conversation-1",
        )

    val request = Request(data = MessageSendParams(message))
    val response = client.sendMessage(request)

    // Handle response
    when (val event = response.data) {
        is Message -> {
            val text =
                event.parts
                    .filterIsInstance<TextPart>()
                    .joinToString { it.text }
            print(text) // Stream partial responses
        }

        is TaskEvent -> {
            println("\nTask completed: $event")
        }
    }
}
