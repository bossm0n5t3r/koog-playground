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

    // Get PromptApi instance from Koin
    val promptApi: PromptApi by inject(PromptApi::class.java)

    // Run the API
//    promptApi.run()

    // Get a SimpleApi instance from Koin
    val singleApi: SingleApi by inject(SingleApi::class.java)

    // Run the SimpleApi
    singleApi.run()
}
