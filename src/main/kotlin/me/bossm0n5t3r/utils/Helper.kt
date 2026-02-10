package me.bossm0n5t3r.utils

suspend fun executeWithTitle(
    title: String,
    block: suspend () -> Unit,
) {
    println("\n")
    println("=== $title ===")
    block.invoke()
}
