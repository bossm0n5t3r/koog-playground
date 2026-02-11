package me.bossm0n5t3r.a2a

import ai.koog.a2a.model.AgentCapabilities
import ai.koog.a2a.model.AgentCard
import ai.koog.a2a.model.AgentSkill
import ai.koog.a2a.model.TransportProtocol
import ai.koog.a2a.server.A2AServer
import ai.koog.a2a.transport.server.jsonrpc.http.HttpJSONRPCServerTransport
import io.ktor.server.cio.CIO

const val PORT = 9999
const val ECHO_AGENT_PATH = "/agent"
const val ECHO_AGENT_CARD_PATH = "$ECHO_AGENT_PATH/agent-card.json"

class SimpleA2AServer {
    val agentCard =
        AgentCard(
            name = "IO Assistant",
            description = "AI agent specialized in input modification",
            version = "2.1.0",
            protocolVersion = "0.3.0",
            // Communication Settings
            url = "http://localhost:$PORT$ECHO_AGENT_PATH",
            preferredTransport = TransportProtocol.JSONRPC,
            // Capabilities Declaration
            capabilities =
                AgentCapabilities(
                    streaming = true, // Support real-time responses
                    pushNotifications = true, // Send async notifications
                    stateTransitionHistory = true, // Maintain a task history
                ),
            // Content Type Support
            defaultInputModes = listOf("text/plain", "text/markdown", "image/jpeg"),
            defaultOutputModes = listOf("text/plain", "text/markdown", "application/json"),
            // Skills/Capabilities
            skills =
                listOf(
                    AgentSkill(
                        id = "echo",
                        name = "echo",
                        description = "Echoes back user messages",
                        tags = listOf("io"),
                    ),
                ),
        )

    val server =
        A2AServer(
            agentExecutor = EchoAgentExecutor(),
            agentCard = agentCard,
        )
}

suspend fun main() {
    // HTTP JSON-RPC transport
    val server = SimpleA2AServer()
    val transport = HttpJSONRPCServerTransport(server.server)
    transport.start(
        engineFactory = CIO,
        port = PORT,
        path = ECHO_AGENT_PATH,
        wait = true,
        agentCard = server.agentCard,
        agentCardPath = ECHO_AGENT_CARD_PATH,
    )
}
