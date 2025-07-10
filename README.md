# Koog Playground

![Kotlin Koog](https://img.shields.io/badge/Kotlin%20Koog%20v0.2.1-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)

This repository contains examples and utilities for working with the Koog AI agent framework.

## Overview

Koog Playground is a project that demonstrates various capabilities of the Koog framework for building AI agents. It includes examples of:

- AI agents implementation
- Strategy patterns for AI interactions
- API integrations
- Tool sets for extending agent capabilities
- Utility functions

## Koog Links

- https://github.com/JetBrains/koog
- https://docs.koog.ai/

## Version

Current version: `0.2.1`

## Project Structure

- `src/main/kotlin/me/bossm0n5t3r/agent/` - AI agent implementations
- `src/main/kotlin/me/bossm0n5t3r/api/` - API integration examples
- `src/main/kotlin/me/bossm0n5t3r/mcp/` - Model Context Protocol implementations
  - Google Maps integration for location-based queries
  - Playwright for browser automation tasks
- `src/main/kotlin/me/bossm0n5t3r/strategies/` - Strategy patterns for AI interactions
- `src/main/kotlin/me/bossm0n5t3r/tools/` - Tool sets for extending agent capabilities
- `src/main/kotlin/me/bossm0n5t3r/utils/` - Utility functions

## Getting Started

### Prerequisites

- JDK 21 or higher
- Gradle

### Setup

1. Clone the repository
2. Build the project:
   ```
   ./gradlew build
   ```
3. Run examples:
   ```
   ./gradlew run
   ```

## Examples

Check out `QuickstartExample.kt` for a simple demonstration of how to use the Koog framework.

## Running the Application

The application provides an interactive menu to run different examples:

1. Run the application:
   ```
   ./gradlew run
   ```

2. Select an example to run from the menu by entering the corresponding number:
   ```
   > Task :run
    00:00:00.249 [main] INFO Application -- Hello, Kotlin!
    00:00:00.311 [main] INFO Application -- Please select the example to run:
    00:00:00.314 [main] INFO Application -- 1. PromptApi
    00:00:00.314 [main] INFO Application -- 2. SingleApi
    00:00:00.314 [main] INFO Application -- 3. AIAgent
    00:00:00.314 [main] INFO Application -- 4. AgentWithWeatherToolSet
    00:00:00.314 [main] INFO Application -- 5. StructuredDataProcessing
    00:00:00.314 [main] INFO Application -- 6. Google Maps
    00:00:00.314 [main] INFO Application -- 7. Playwright
    00:00:00.314 [main] INFO Application -- 0. Exit
    Enter your choice:
    <==========---> 83%00:00:00.773 [main] INFO Application --
   ```

3. Follow the prompts for the selected example.

Each example demonstrates different capabilities of the Koog framework:
- **PromptApi**: Shows how to use the prompt API for generating responses
- **SingleApi**: Demonstrates single API calls to LLM models
- **AIAgent**: Basic AI agent implementation
- **AgentWithWeatherToolSet**: AI agent with weather-related tools
- **StructuredDataProcessing**: Processing structured data with AI
- **Google Maps**: Using Google Maps MCP for location-based queries
- **Playwright**: Browser automation using Playwright MCP

## Dependencies

This project uses:
- Koog Agents library
- Koin for dependency injection
- KotlinX Serialization for JSON handling
- JUnit 5 and MockK for testing
