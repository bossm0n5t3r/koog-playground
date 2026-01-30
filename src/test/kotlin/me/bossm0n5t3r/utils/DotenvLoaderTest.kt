package me.bossm0n5t3r.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DotenvLoaderTest {
    private val testEnvPath = "src/test/resources/.env.test"

    @Test
    fun `load should parse env file correctly`() {
        val result = DotenvLoader.load(testEnvPath)

        assertEquals("test_value", result["TEST_KEY"])
        assertEquals("quoted_value", result["QUOTED_KEY"])
        assertEquals("single_quoted_value", result["SINGLE_QUOTED_KEY"])
        assertEquals("spaced_value", result["SPACED_KEY"])
        assertNull(result["NON_EXISTENT"])
    }

    @Test
    fun `load should return empty map if file does not exist`() {
        val result = DotenvLoader.load("non_existent.env")
        assertEquals(emptyMap(), result)
    }

    @Test
    fun `get should return value from default env file or system env`() {
        // DotenvLoader.init loads ".env" which might or might not exist in the root.
        // If it exists, it will be in envMap.

        // We can't easily mock System.getenv() without PowerMock or similar,
        // but we can check if it returns something from System.getenv() for a known key.
        val pathValue = System.getenv("PATH")
        if (pathValue != null) {
            assertEquals(pathValue, DotenvLoader["PATH"])
        }
    }
}
