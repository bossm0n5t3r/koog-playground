package me.bossm0n5t3r.api

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertTrue

class PromptApiTest {
    private val sut = PromptApi()

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
