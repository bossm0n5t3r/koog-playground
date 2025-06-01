package me.bossm0n5t3r.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("Tools for getting weather information")
class WeatherToolSet : ToolSet {
    @Tool
    @LLMDescription("Get the current weather for a location")
    fun getWeather(
        @LLMDescription("The city and state/country")
        location: String,
    ): String {
        // In a real implementation, you would call a weather API
        return "The weather in $location is sunny and 72°F"
    }
}
