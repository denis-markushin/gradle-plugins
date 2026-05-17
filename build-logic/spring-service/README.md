# spring-service

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.spring-service?label=spring-service)](https://plugins.gradle.org/plugin/io.github.denis-markushin.spring-service)

An opinionated convention plugin for production-ready Spring Boot microservices. Applying it is equivalent to wiring together a curated set
of plugins and dependencies that would otherwise require boilerplate configuration in every service.

---

## What it configures

| Area                   | Details                                                                                                                                                                                         |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin baseline**    | JVM toolchain, compiler args, and repositories are inherited from [`io.github.denis-markushin.kotlin`](../kotlin/README.md) (applied automatically)                                             |
| **Spring Boot**        | `spring-boot` + `kotlin.plugin.spring` applied; Spring Boot BOM imported into all dependency configurations; `spring-boot-devtools` and `spring-boot-docker-compose` added to `developmentOnly` |
| **Build info**         | `spring-boot-gradle-plugin` build info task configured so `management.info.build.*` is populated at runtime                                                                                     |
| **Resource filtering** | `processResources` substitutes `@project.*@` tokens from `rootProject.properties` into `*.yml`, `*.yaml`, and `*.properties` files                                                              |
| **Testing**            | JUnit Platform enabled; `spring-boot-test`, `assertk`, `spring-mockk`, `kotest`, `awaitility` added; Mockito excluded globally                                                                  |
| **MapStruct**          | Optional — enabled via `platform.useMapstruct = true`; adds `mapstruct-core`, `mapstruct-spring-annotations`, and kapt processors with Spring component model                                   |
| **jcabi-aspects**      | Optional — enabled by default; can be disabled via `platform.useJcabi = false`                                                                                                                  |
| **Spotless**           | ktlint applied to all `*.kt` and `*.gradle.kts` sources; `.editorconfig` and `.gitattributes` bootstrapped into the root project if absent; pre-push Git hook installed                         |
| **Versioning**         | [Vercraft](https://github.com/akuleshov7/vercraft) plugin applied for semantic versioning from Git tags                                                                                         |
| **Scaffold files**     | `Dockerfile.build-image`, `.dockerignore`, and `.gitlab-ci.yml` are written to the project root if absent                                                                                       |

---

## Installation

```kotlin
// settings.gradle.kts
pluginManagement {
  repositories {
    gradlePluginPortal()
  }
}
```

```kotlin
// build.gradle.kts
plugins {
  id("io.github.denis-markushin.spring-service") version "x.x.x"
}
```

```groovy
// build.gradle
plugins {
  id 'io.github.denis-markushin.spring-service' version 'x.x.x'
}
```

---

## Configuration

The plugin exposes a `platform` extension for opt-in features:

```kotlin
platform {
  useKapt = true          // default: true  — apply kotlin-kapt
  useJcabi = true         // default: true  — wire jcabi-aspects weaving
  useMapstruct = false    // default: false — add MapStruct dependencies and kapt processors

  spring {
    useCloud = false    // default: false — import Spring Cloud BOM

    netflixDgs {
      useNetflixDgs = false   // default: false — import Netflix DGS BOM and apply codegen plugin

      generator {
        // All fields below are true/enabled by default when useNetflixDgs = true
        generateDocs = true
        generateClient = true
        generateBoxedTypes = true
        snakeCaseConstantNames = true
        addGeneratedAnnotation = true
        generateKotlinNullableClasses = true
        generateKotlinClosureProjections = true

        // Default type mappings (can be extended)
        typeMapping = mapOf(
          "UUID" to "java.util.UUID",
          "Generated" to "jakarta.annotation.Generated",
          "LocalDateTime" to "java.time.LocalDateTime",
          "Upload" to "org.springframework.web.multipart.MultipartFile",
        )
      }
    }
  }
}
```

---

## Resource token filtering

Properties defined in `rootProject.properties` are available as `@project.<key>@` tokens inside resource files:

```yaml
# application.yml
spring:
  application:
    name: "@project.name@"
```
