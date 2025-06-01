import me.bossm0n5t3r.agent.AIAgentSample
import me.bossm0n5t3r.api.PromptApi
import me.bossm0n5t3r.api.SingleApi
import me.bossm0n5t3r.di.appModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

suspend fun main() {
    println("Hello, Kotlin!")

    // Start Koin
    startKoin {
        modules(appModule)
    }

    // PromptApi
    val promptApi: PromptApi by inject(PromptApi::class.java)
//    promptApi.run()

    // SimpleApi
    val singleApi: SingleApi by inject(SingleApi::class.java)
//    singleApi.run()

    // AIAgent
    val aiAgent: AIAgentSample by inject(AIAgentSample::class.java)
    aiAgent.main()
}
