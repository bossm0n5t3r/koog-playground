package me.bossm0n5t3r.utils

import java.io.File

object DotenvLoader {
    private val envMap: Map<String, String>

    init {
        envMap = load(".env")
    }

    fun load(path: String): Map<String, String> {
        val file = File(path)
        if (file.exists().not()) return emptyMap()

        return buildMap {
            file.readLines().forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                    val parts = trimmedLine.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim().removeSurrounding("\"", "\"").removeSurrounding("'", "'")
                        put(key, value)
                    }
                }
            }
        }
    }

    operator fun get(key: String): String? = envMap[key] ?: System.getenv(key)
}
