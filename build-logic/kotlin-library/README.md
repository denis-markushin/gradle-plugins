# kotlin-library

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/org.demalab.kotlin-library?label=kotlin-library)](https://plugins.gradle.org/plugin/org.demalab.kotlin-library)

- [What it configures](#what-it-configures)
- [Installation](#installation)

A lightweight convention plugin for reusable Kotlin library projects.

---

## What it configures

| Area                | Details                                                                                                                               |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin baseline** | JVM toolchain, compiler args, and repositories are inherited from [`org.demalab.kotlin`](../kotlin/README.md) (applied automatically) |
| **Java**            | `java-library` plugin applied; `-sources.jar` and `-javadoc.jar` artifacts produced                                                   |
| **Publishing**      | `maven-publish` applied; `mavenJava` publication created from the `java` component                                                    |
| **Coverage**        | Kover on `check` with generated code excluded — inherited from [`org.demalab.kotlin`](../kotlin/README.md#coverage)                   |

---

## Installation

```kotlin
// build.gradle.kts
plugins {
  id("org.demalab.kotlin-library") version "x.x.x"
}
```

```groovy
// build.gradle
plugins {
  id 'org.demalab.kotlin-library' version 'x.x.x'
}
```
