package me.bossm0n5t3r.strategies

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.message.Message
import ai.koog.prompt.structure.StructureFixingParser
import ai.koog.prompt.structure.StructuredOutput
import ai.koog.prompt.structure.StructuredOutputConfig
import ai.koog.prompt.structure.json.JsonStructuredData
import ai.koog.prompt.structure.json.generator.BasicJsonSchemaGenerator
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.bossm0n5t3r.me.bossm0n5t3r.utils.executeWithTitle

class StructuredDataProcessing(
    private val openAIApiKey: String,
) {
    @Serializable
    @SerialName("SimpleWeatherForecast")
    @LLMDescription("Simple weather forecast for a location")
    data class SimpleWeatherForecast(
        @property:LLMDescription("Location name")
        val location: String,
        @property:LLMDescription("Temperature in Celsius")
        val temperature: Int,
        @property:LLMDescription("Weather conditions (e.g., sunny, cloudy, rainy)")
        val conditions: String,
    )

    fun main(): Unit =
        runBlocking {
            // Create sample forecasts
            val exampleForecasts =
                listOf(
                    SimpleWeatherForecast(
                        location = "New York",
                        temperature = 25,
                        conditions = "Sunny",
                    ),
                    SimpleWeatherForecast(
                        location = "London",
                        temperature = 18,
                        conditions = "Cloudy",
                    ),
                )

            // Generate JSON Schema
            val forecastsStructure =
                JsonStructuredData.createJsonStructure<SimpleWeatherForecast>(
                    schemaGenerator = BasicJsonSchemaGenerator.Default,
                    examples = exampleForecasts,
                )

            // Define the agent strategy
            val agentStrategy =
                strategy("weather-forecast") {
                    val setup by nodeLLMRequest()

                    val getStructuredForecast by node<Message.Response, String> { _ ->
                        val structuredResponse =
                            llm.writeSession {
                                this.requestLLMStructured(
                                    config =
                                        StructuredOutputConfig(
                                            default = StructuredOutput.Manual(forecastsStructure),
                                            fixingParser =
                                                StructureFixingParser(
                                                    fixingModel = OpenAIModels.Chat.GPT4o,
                                                    retries = 3,
                                                ),
                                        ),
                                )
                            }

                        """
                        Response structure:
                        $structuredResponse
                        """.trimIndent()
                    }

                    edge(nodeStart forwardTo setup)
                    edge(setup forwardTo getStructuredForecast)
                    edge(getStructuredForecast forwardTo nodeFinish)
                }

            // Configure and run the agent
            val agentConfig =
                AIAgentConfig(
                    prompt =
                        prompt("weather-forecast-prompt") {
                            system(
                                """
                                You are a weather forecasting assistant.
                                When asked for a weather forecast, provide a realistic but fictional forecast.
                                """.trimIndent(),
                            )
                            user(
                                """
                                Please provide a list of 10 weather forecasts for Paris.
                                """.trimIndent(),
                            )
                        },
                    model = OpenAIModels.Chat.GPT4o,
                    maxAgentIterations = 5,
                )

            val runner =
                AIAgent(
                    promptExecutor = simpleOpenAIExecutor(openAIApiKey),
                    toolRegistry = ToolRegistry.EMPTY,
                    strategy = agentStrategy,
                    agentConfig = agentConfig,
                )

            executeWithTitle("Structured data processing") {
                val responseString = runner.run("Get weather forecast for Paris")
                println(responseString)

                val regex = Regex("""raw=\[(.|\n)*]""")
                val rawMatch = regex.find(responseString)
                val rawJson =
                    rawMatch
                        ?.value
                        ?.removePrefix("raw=")
                        ?.trim()

                println("Raw JSON:")
                println(rawJson)

                // JSON deserialization
                val forecast =
                    rawJson?.let {
                        Json.decodeFromString<List<SimpleWeatherForecast>>(it)
                    }
                println("Forecast:")
                println(forecast)
            }
        }
}
