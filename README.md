# Gradle Plugins

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)

A multi-module repository hosting opinionated Gradle convention plugins published under the `io.github.denis-markushin` group on
the [Gradle Plugin Portal](https://plugins.gradle.org/u/denis-markushin).

## Plugins

| Plugin ID                                                                                    | Module                                                           | Description                                                                    |
|----------------------------------------------------------------------------------------------|------------------------------------------------------------------|--------------------------------------------------------------------------------|
| [`io.github.denis-markushin.spring-service`](build-logic/spring-service/README.md)           | [spring-service](build-logic/spring-service/README.md)           | Convention plugin for production-ready Spring Boot microservices               |
| [`io.github.denis-markushin.kotlin-library`](build-logic/kotlin-library/README.md)           | [kotlin-library](build-logic/kotlin-library/README.md)           | Convention plugin for Kotlin library projects                                  |
| [`io.github.denis-markushin.kotlin`](build-logic/kotlin/README.md)                           | [kotlin](build-logic/kotlin/README.md)                           | Foundational Kotlin convention plugin (toolchain, compiler args, repositories) |
| [`io.github.denis-markushin.spotless`](build-logic/spotless/README.md)                       | [spotless](build-logic/spotless/README.md)                       | Spotless code formatting convention plugin                                     |
| [`io.github.denis-markushin.jcabi-gradle-plugin`](build-logic/jcabi-gradle-plugin/README.md) | [jcabi-gradle-plugin](build-logic/jcabi-gradle-plugin/README.md) | Wires jcabi-aspects AspectJ post-compile weaving into your build               |
| [`io.github.denis-markushin.jooq-codegen`](build-logic/jooq-codegen-gradle-plugin/README.md) | [jooq-codegen](build-logic/jooq-codegen-gradle-plugin/README.md) | Generates jOOQ sources from a Liquibase changelog via Testcontainers           |

## Requirements

- Java 21
- Gradle 8+

## Modules

- [spring-service](build-logic/spring-service/README.md) — Convention plugin for production-ready Spring Boot microservices
- [kotlin-library](build-logic/kotlin-library/README.md) — Convention plugin for Kotlin library projects
- [kotlin](build-logic/kotlin/README.md) — Foundational Kotlin convention plugin (toolchain, compiler args, repositories)
- [spotless](build-logic/spotless/README.md) — Spotless code formatting convention plugin
- [jcabi-gradle-plugin](build-logic/jcabi-gradle-plugin/README.md) — jcabi-aspects AspectJ weaving plugin
- [jooq-codegen](build-logic/jooq-codegen-gradle-plugin/README.md) — Generates jOOQ sources from a Liquibase changelog via Testcontainers

## Cutting a release

Releases are cut from the GitHub **Actions** tab using one of three manual workflows:

- **Release MAJOR** / **Release MINOR** — run from the `master` branch.
- **Release PATCH** — run from a `release/*` branch.

Each workflow runs the vercraft `makeRelease` task, which creates the release branch/tag and pushes it. The
tag push triggers `publish-plugin.yaml`, which publishes the plugin to the Gradle Plugin Portal.
