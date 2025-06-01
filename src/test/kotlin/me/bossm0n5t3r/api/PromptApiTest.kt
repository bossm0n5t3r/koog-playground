package me.bossm0n5t3r.api

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertTrue

class PromptApiTest {
    // Create test instances for dependencies
    private val openAIApiKey = "test-openai-api-key"
    private val openAILLMClient = OpenAILLMClient("test-openai-api-key")
    private val googleAIStudioLLMClient = GoogleLLMClient("test-google-api-key")

    // Create the class under test with the test dependencies
    private val sut = PromptApi(openAIApiKey, openAILLMClient, googleAIStudioLLMClient)

    @Test
    fun `test createPrompt creates a valid prompt`() {
        // When
        val prompt = sut.createPrompt()

        // Then
        assertNotNull(prompt)
        assertTrue { prompt.toString().isNotEmpty() }

        // We can verify that the prompt is created without errors
        assertDoesNotThrow { prompt.toString() }
    }
}
