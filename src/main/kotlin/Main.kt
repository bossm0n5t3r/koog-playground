import me.bossm0n5t3r.LOGGER
import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.di.appModule
import me.bossm0n5t3r.mcp.GoogleMaps
import me.bossm0n5t3r.mcp.Playwright
import me.bossm0n5t3r.strategies.StructuredDataProcessing
import me.bossm0n5t3r.tools.AgentWithWeatherToolSet
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

suspend fun main() = Application.run()

object Application {
    suspend fun run() {
        LOGGER.info("Hello, Kotlin!")

        // Start Koin
        startKoin {
            modules(appModule)
        }

        // 실행할 예제들을 맵으로 관리
        val examples =
            mapOf<String, suspend () -> Unit>(
                "PromptApi" to {
                    val api: PromptApi by inject(PromptApi::class.java)
                    api.run()
                },
                "SingleApi" to {
                    val api: SingleApi by inject(SingleApi::class.java)
                    api.run()
                },
                "AIAgent" to {
                    val agent: AIAgentSample by inject(AIAgentSample::class.java)
                    agent.main()
                },
                "AgentWithWeatherToolSet" to {
                    val agent: AgentWithWeatherToolSet by inject(AgentWithWeatherToolSet::class.java)
                    agent.run()
                },
                "StructuredDataProcessing" to {
                    val processor: StructuredDataProcessing by inject(StructuredDataProcessing::class.java)
                    processor.main()
                },
                "Google Maps" to {
                    val googleMaps: GoogleMaps by inject(GoogleMaps::class.java)
                    googleMaps.run()
                },
                "Playwright" to {
                    val playwright: Playwright by inject(Playwright::class.java)
                    playwright.run()
                },
            )

        while (true) {
            LOGGER.info("Please select the example to run:")
            val exampleKeys = examples.keys.toList()
            exampleKeys.forEachIndexed { index, name ->
                LOGGER.info("${index + 1}. $name")
            }
            LOGGER.info("0. Exit")

            print("Enter your choice: ")
            when (val choice = readlnOrNull()?.toIntOrNull()) {
                null -> {
                    LOGGER.info("Invalid input. Please enter a number.")
                }

                in 1..exampleKeys.size -> {
                    val selectedKey = exampleKeys[choice - 1]
                    LOGGER.info("\n--- Running $selectedKey ---")
                    examples[selectedKey]?.invoke()
                    LOGGER.info("--- $selectedKey finished ---\n")
                }

                0 -> {
                    LOGGER.info("Exiting.")
                    return // main 함수 종료
                }

                else -> {
                    LOGGER.info("Invalid choice. Please try again.")
                }
            }
        }
    }
}
