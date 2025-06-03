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
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
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

    @Serializable
    data class StructuredResponse<T>(
        val structure: T,
        val raw: String,
    )

    @Serializable
    data class Success<T>(
        val data: T,
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
            val forecastStructure =
                JsonStructuredData.createJsonStructure<SimpleWeatherForecast>(
                    schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
                    examples = exampleForecasts,
                    schemaType = JsonStructuredData.JsonSchemaType.SIMPLE,
                )

            // Define the agent strategy
            val agentStrategy =
                strategy("weather-forecast") {
                    val setup by nodeLLMRequest()

                    val getStructuredForecast by node<Message.Response, String> { _ ->
                        val structuredResponse =
                            llm.writeSession {
                                this.requestLLMStructured(
                                    structure = forecastStructure,
                                    fixingModel = OpenAIModels.Chat.GPT4o,
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
                val responseString = runner.runAndGetResult("Get weather forecast for Paris")
                println(responseString)

                if (responseString == null) return@executeWithTitle

                val regex = Regex("""raw=\{(.|\n)*\}""")
                val rawMatch = regex.find(responseString)
                val rawJson =
                    rawMatch
                        ?.value
                        ?.removePrefix("raw=")
                        ?.trim()

                // JSON 역직렬화
                val forecast =
                    rawJson?.let {
                        Json.decodeFromString<SimpleWeatherForecast>(it)
                    }
                println(forecast)
            }
        }
}
