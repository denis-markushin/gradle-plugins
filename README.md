# Gradle Plugins

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/org.demalab.spring-service?label=plugin%20portal)](https://plugins.gradle.org/u/dmarkushin)
[![License](https://img.shields.io/github/license/denis-markushin/gradle-plugins)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-25%2B-blue)](#requirements)

- [Plugins](#plugins)
- [Quick start](#quick-start)
- [Requirements](#requirements)
- [Cutting a release](#cutting-a-release)
- [License](#license)

A multi-module repository hosting opinionated Gradle convention plugins published under the `org.demalab` group on
the [Gradle Plugin Portal](https://plugins.gradle.org/u/dmarkushin).

## Plugins

| Plugin ID                                                                      | Description                                                                    |
|--------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| [`org.demalab.spring-service`](build-logic/spring-service/README.md)           | Convention plugin for production-ready Spring Boot microservices               |
| [`org.demalab.kotlin-library`](build-logic/kotlin-library/README.md)           | Convention plugin for Kotlin library projects                                  |
| [`org.demalab.kotlin`](build-logic/kotlin/README.md)                           | Foundational Kotlin convention plugin (toolchain, compiler args, repositories) |
| [`org.demalab.spotless`](build-logic/spotless/README.md)                       | Spotless code formatting convention plugin                                     |
| [`org.demalab.jcabi`](build-logic/jcabi-gradle-plugin/README.md)               | Wires jcabi-aspects AspectJ post-compile weaving into your build               |
| [`org.demalab.jooq-codegen`](build-logic/jooq-codegen-gradle-plugin/README.md) | Generates jOOQ sources from a Liquibase changelog via Testcontainers           |

## Quick start

All plugins are published to the Gradle Plugin Portal, so no extra repository is needed:

```kotlin
// build.gradle.kts
plugins {
  id("org.demalab.spring-service") version "2.0.0"
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
