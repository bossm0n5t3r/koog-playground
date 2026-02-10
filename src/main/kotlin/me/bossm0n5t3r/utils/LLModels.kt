package me.bossm0n5t3r.utils

import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel

object LLModels {
    val QWEN_2_5_7B =
        LLModel(
            provider = LLMProvider.Ollama,
            id = "qwen2.5:7b",
            capabilities =
                listOf(
                    LLMCapability.Temperature,
                    LLMCapability.Schema.JSON.Basic,
                    LLMCapability.Tools,
                ),
            contextLength = 32_768,
        )
}
