package me.bossm0n5t3r.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import me.bossm0n5t3r.LOGGER
import me.bossm0n5t3r.utils.LLModels.QWEN_2_5_7B

class OllamaAgentSample {
    suspend fun run() {
        LOGGER.info("Starting OllamaAgentSample with qwen2.5:7b model using OpenAI compatible API...")

        val agent =
            AIAgent(
                // Ollama often provides an OpenAI-compatible endpoint at http://localhost:11434/v1
                // We use the API key to construct the executor.
                promptExecutor = simpleOllamaAIExecutor(),
                systemPrompt = "You are a helpful assistant. Answer user questions concisely in Korean.",
                llmModel = QWEN_2_5_7B,
            )

        println("Ollama (qwen2.5:7b) 에이전트에게 질문을 입력하세요 (종료하려면 'exit' 입력):")

        while (true) {
            print("User: ")
            val userInput = readlnOrNull() ?: ""
            if (userInput.lowercase() == "exit") break
            if (userInput.isBlank()) continue

            try {
                val result = agent.run(userInput)
                println("\nAgent: $result\n")
            } catch (e: Exception) {
                LOGGER.error("Ollama 에이전트 실행 중 오류 발생: ${e.message}")
                println("오류가 발생했습니다. Ollama가 로컬에서 실행 중인지, qwen2.5:7b 모델이 다운로드되어 있는지 확인해주세요.")
                break
            }
        }
    }
}
