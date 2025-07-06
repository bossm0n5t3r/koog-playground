import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.di.appModule
import me.bossm0n5t3r.strategies.StructuredDataProcessing
import me.bossm0n5t3r.tools.AgentWithWeatherToolSet
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

suspend fun main() {
    println("Hello, Kotlin!")

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
        )

    while (true) {
        println("\nPlease select the example to run:")
        val exampleKeys = examples.keys.toList()
        exampleKeys.forEachIndexed { index, name ->
            println("${index + 1}. $name")
        }
        println("0. Exit")

        print("Enter your choice: ")
        val choice = readlnOrNull()?.toIntOrNull()

        when (choice) {
            null -> {
                println("Invalid input. Please enter a number.")
            }
            in 1..exampleKeys.size -> {
                val selectedKey = exampleKeys[choice - 1]
                println("\n--- Running $selectedKey ---")
                examples[selectedKey]?.invoke()
                println("--- $selectedKey finished ---\n")
            }
            0 -> {
                println("Exiting.")
                return // main 함수 종료
            }
            else -> {
                println("Invalid choice. Please try again.")
            }
        }
    }
}
