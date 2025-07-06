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

## Dependencies

This project uses:
- Koog Agents library
- Koin for dependency injection
- KotlinX Serialization for JSON handling
- JUnit 5 and MockK for testing
