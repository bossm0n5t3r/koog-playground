# Koog Playground

![Kotlin Koog](https://img.shields.io/badge/Kotlin%20Koog%20v0.6.0-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)

This repository contains examples and utilities for working with
the [Koog AI agent framework](https://github.com/JetBrains/koog).

## Overview

Koog Playground demonstrates various capabilities of the Koog framework for building AI agents, including:

- **AI Agents**: Implementations of autonomous agents.
- **Strategy Patterns**: Custom graphs, history compression, and structured data processing.
- **MCP (Model Context Protocol)**: Integrations with Google Maps and Playwright.
- **Tool Sets**: Extending agent capabilities with weather tools and diagnostics.
- **Observability**: OpenTelemetry and Langfuse tracing integration.

## Requirements

- **JDK 25** or higher (configured in `libs.versions.toml`)
- **Gradle** (wrapper included)
- **Docker** (optional, for Langfuse tracing)

## Project Structure

```text
.
├── build.gradle.kts          # Project build configuration
├── gradle/libs.versions.toml # Dependency and version management
├── src
│   ├── main/kotlin
│   │   ├── Main.kt           # Main entry point with interactive menu
│   │   ├── QuickstartExample.kt
│   │   └── me/bossm0n5t3r
│   │       ├── agent/        # Agent implementations
│   │       ├── api/          # LLM API examples
│   │       ├── di/           # Koin dependency injection
│   │       ├── mcp/          # Model Context Protocol tools
│   │       ├── opentelemetry/# Tracing and observability
│   │       ├── strategies/   # AI interaction strategies
│   │       ├── tools/        # Agent tool sets
│   │       └── utils/        # General utilities
│   └── test/kotlin           # Unit tests
└── LICENSE
```

## Setup & Run

### 1. Environment Variables

Create a `.env` file or export the following variables in your terminal:

```bash
export OPENAI_API_KEY="your_openai_key"
export GOOGLE_AI_STUDIO_API_KEY="your_google_ai_key"
export GOOGLE_MAPS_API_KEY="your_google_maps_key"
```

### 2. Build

```bash
./gradlew build
```

### 3. Run

The project provides an interactive menu to explore different examples:

```bash
./gradlew run
```

Alternatively, you can run specific examples if they have a `main` function (e.g., `QuickstartExample.kt`).

## Available Examples

When running the application, you can choose from:

1. **PromptApi**: High-level prompt API usage.
2. **SingleApi**: Direct LLM API calls.
3. **AIAgent**: Core agent functionality.
4. **AgentWithWeatherToolSet**: Agent using external tools.
5. **StructuredDataProcessing**: Extracting structured info using AI.
6. **Google Maps**: MCP integration for location data.
7. **Playwright**: MCP integration for browser automation.

### Tracing with Langfuse

To try the Langfuse tracing example:

1. Navigate to `src/main/kotlin/me/bossm0n5t3r/opentelemetry/langfuse/`.
2. Start Langfuse using Docker:
   ```bash
   docker-compose up -d
   ```
3. Configure the `.env` file in that directory with your Langfuse credentials.
4. Run `AgentWithLangfuseTracing.kt`.

## Scripts

- `./gradlew run`: Start the interactive menu.
- `./gradlew test`: Run all tests.
- `./gradlew ktlintCheck`: Check code style.
- `./gradlew ktlintFormat`: Auto-format code.

## Testing

Tests are written using JUnit 5 and MockK.

```bash
./gradlew test
```

## Koog Links

- [Official Repository](https://github.com/JetBrains/koog)
- [Documentation](https://docs.koog.ai/)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
