# kotlin

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.kotlin?label=kotlin)](https://plugins.gradle.org/plugin/io.github.denis-markushin.kotlin)

- [What it configures](#what-it-configures)
- [Installation](#installation)
- [Coverage](#coverage)

A foundational Kotlin convention plugin. Applies `org.jetbrains.kotlin.jvm` and configures the JVM toolchain, compiler arguments, and
standard repositories. It is the shared baseline used by `io.github.denis-markushin.kotlin-library` and
`io.github.denis-markushin.spring-service`; you can also apply it directly to any Kotlin/JVM project that does not need the higher-level
conventions.

---

## What it configures

| Area              | Details                                                                                                                                           |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin plugin** | Applies `org.jetbrains.kotlin.jvm`                                                                                                                |
| **Toolchain**     | JVM toolchain set to Java 25                                                                                                                      |
| **Compiler args** | `-Xjsr305=strict`, `-Xemit-jvm-type-annotations`, `javaParameters=true`, `allWarningsAsErrors=true` |
| **Repositories**  | `mavenLocal()`, `mavenCentral()` (in that order)                                                                                                  |
| **Coverage**      | Kover applied; XML (JaCoCo schema, `build/reports/kover/report.xml`) and log reports run on `check`; generated code excluded (see [Coverage](#coverage)) |

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

---

## Coverage

Kover measures line coverage on `check`: the XML report in the JaCoCo schema lands in `build/reports/kover/report.xml` and the log prints
`application line coverage: NN%`. Generated code is excluded by default: classes annotated with any `*Generated` annotation that survives
compilation (Netflix DGS codegen, jOOQ with `org.jooq.Generated`) and MapStruct implementations by name (`*Impl`,
`*ConversionServiceAdapter`), since MapStruct's `javax.annotation.processing.Generated` has source retention and is invisible in bytecode.

A GitLab job gets the percentage and the diff annotation with:

```yaml
test:
  script:
    - ./gradlew check
  coverage: '/application line coverage: (\d+(?:\.\d+)?)%/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: jacoco
        path: build/reports/kover/report.xml
```

A project that needs more exclusions appends to Kover's own extension:

```kotlin
kover {
    reports {
        filters {
            excludes {
                classes("com.example.legacy.*")
            }
        }
    }
}
```
