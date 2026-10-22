# Gradle Plugins

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.spring-service?label=plugin%20portal)](https://plugins.gradle.org/u/dmarkushin)
[![License](https://img.shields.io/github/license/denis-markushin/gradle-plugins)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-25%2B-blue)](#requirements)

- [Plugins](#plugins)
- [Quick start](#quick-start)
- [Requirements](#requirements)
- [Cutting a release](#cutting-a-release)
- [License](#license)

A multi-module repository hosting opinionated Gradle convention plugins published under the `io.github.denis-markushin` group on
the [Gradle Plugin Portal](https://plugins.gradle.org/u/dmarkushin).

## Plugins

| Plugin ID                                                                                    | Description                                                                    |
|----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| [`io.github.denis-markushin.spring-service`](build-logic/spring-service/README.md)           | Convention plugin for production-ready Spring Boot microservices               |
| [`io.github.denis-markushin.kotlin-library`](build-logic/kotlin-library/README.md)           | Convention plugin for Kotlin library projects                                  |
| [`io.github.denis-markushin.kotlin`](build-logic/kotlin/README.md)                           | Foundational Kotlin convention plugin (toolchain, compiler args, repositories) |
| [`io.github.denis-markushin.spotless`](build-logic/spotless/README.md)                       | Spotless code formatting convention plugin                                     |
| [`io.github.denis-markushin.jcabi-gradle-plugin`](build-logic/jcabi-gradle-plugin/README.md) | Wires jcabi-aspects AspectJ post-compile weaving into your build               |
| [`io.github.denis-markushin.jooq-codegen`](build-logic/jooq-codegen-gradle-plugin/README.md) | Generates jOOQ sources from a Liquibase changelog via Testcontainers           |

## Quick start

All plugins are published to the Gradle Plugin Portal, so no extra repository is needed:

```kotlin
// build.gradle.kts
plugins {
  id("io.github.denis-markushin.spring-service") version "1.5.0"
}
```

Each plugin's README lists what it configures and which options it exposes.

## Requirements

- Java 25 — the Gradle daemon itself must run on JDK 25 or newer
- Gradle 9+

## Cutting a release

Releases are cut from the GitHub **Actions** tab with the manual **Release** workflow, which asks for a release type:

- **MAJOR** / **MINOR** — run from the `master` branch.
- **PATCH** — run from a `release/*` branch.

The workflow runs the vercraft `makeRelease` task, which creates the release branch/tag and pushes it. The tag push triggers
`publish-plugin.yml`, which publishes the plugins to the Gradle Plugin Portal.

## License

[Apache License 2.0](LICENSE)
