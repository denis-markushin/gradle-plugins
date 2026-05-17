# kotlin

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.kotlin?label=kotlin)](https://plugins.gradle.org/plugin/io.github.denis-markushin.kotlin)

A foundational Kotlin convention plugin. Applies `org.jetbrains.kotlin.jvm` and configures the JVM toolchain, compiler arguments, and
standard repositories. It is the shared baseline used by `io.github.denis-markushin.kotlin-library` and
`io.github.denis-markushin.spring-service`; you can also apply it directly to any Kotlin/JVM project that does not need the higher-level
conventions.

---

## What it configures

| Area              | Details                                                                                                                                           |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin plugin** | Applies `org.jetbrains.kotlin.jvm`                                                                                                                |
| **Toolchain**     | JVM toolchain set to Java 21                                                                                                                      |
| **Compiler args** | `-Xjsr305=strict`, `-Xemit-jvm-type-annotations`, `-Xannotation-default-target=param-property`, `javaParameters=true`, `allWarningsAsErrors=true` |
| **Repositories**  | `mavenLocal()`, `mavenCentral()` (in that order)                                                                                                  |

---

## Installation

```kotlin
// build.gradle.kts
plugins {
  id("io.github.denis-markushin.kotlin") version "x.x.x"
}
```

```groovy
// build.gradle
plugins {
  id 'io.github.denis-markushin.kotlin' version 'x.x.x'
}
```

> If you also apply `io.github.denis-markushin.kotlin-library` or `io.github.denis-markushin.spring-service`, do not apply this plugin
> separately — it is already pulled in.
