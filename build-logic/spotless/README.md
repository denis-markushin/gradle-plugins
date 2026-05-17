# spotless

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.spotless?label=spotless)](https://plugins.gradle.org/plugin/io.github.denis-markushin.spotless)

A standalone Spotless convention plugin. Apply it at the root project level of a multi-module build when you need Spotless formatting
without pulling in the full `spring-service` or `kotlin-library` plugins.

---

## What it configures

| Area               | Details                                                                                       |
|--------------------|-----------------------------------------------------------------------------------------------|
| **ktlint**         | Applied to `**/src/**/*.kt` and `*.gradle.kts` via Spotless                                   |
| **Line endings**   | Determined by `.gitattributes` (`GIT_ATTRIBUTES_FAST_ALLSAME`)                                |
| **Scaffold files** | `.editorconfig` and `.gitattributes` written to `rootDir` if absent (never overwritten)       |
| **Git hook**       | `spotlessInstallGitPrePushHook` wired into the `assemble` task; installs a pre-push hook once |

---

## Installation

Apply at the **root project level** in multi-module builds so that glob patterns cover all submodule sources.

```kotlin
// root build.gradle.kts
plugins {
  id("io.github.denis-markushin.spotless") version "x.x.x"
}
```

```groovy
// root build.gradle
plugins {
  id 'io.github.denis-markushin.spotless' version 'x.x.x'
}
```

---

## Usage

```bash
# Check formatting
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```
