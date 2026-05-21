# jooq-codegen

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.jooq-codegen?label=jooq-codegen)](https://plugins.gradle.org/plugin/io.github.denis-markushin.jooq-codegen)

A convention plugin that generates jOOQ sources from a Liquibase changelog. It spins up a throwaway
PostgreSQL container at codegen time (via Testcontainers JDBC), applies the changelog to it, and
generates type-safe jOOQ classes against the resulting schema — no live database or hand-written
jOOQ XML required.

---

## What it configures

| Area                   | Details                                                                                                                       |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| **Runtime dependency** | `org.jooq:jooq` added to `implementation` (version from `jooqVersion`)                                                        |
| **Codegen classpath**  | A `jooqCodegen` configuration holding the JDBC driver, the Testcontainers/Liquibase database provider, and codegen extensions |
| **Codegen task**       | A `jooqCodegen` task (group `jooq`) that runs the generator in an isolated child classloader; marked `@CacheableTask`         |
| **Source set**         | A dedicated `jooq` source set; generated sources go to `build/generated/sources/jooq` and are kept out of `main`              |
| **Classpath wiring**   | `jooq.output` is added to the compile and runtime classpaths of `main` and `test`, and bundled into every `Jar`               |
| **Task dependencies**  | `compileJooqJava`, `compileKotlin`, and `kaptGenerateStubs*` are wired to depend on `jooqCodegen`                             |

The generated package is derived from the project `group` with dashes stripped
(e.g. `com.acme-demo` → `com.acmedemo`).

---

## Prerequisites

- A `java` (or `java-library`) plugin must be applied — the plugin wires generated sources into the
  Java source sets.
- A Liquibase master changelog at `src/main/resources/liquibase/changelog-master.yml`.
- Docker available on the build machine (Testcontainers starts a PostgreSQL container during codegen).

---

## Installation

```kotlin
// build.gradle.kts
plugins {
  id("io.github.denis-markushin.jooq-codegen") version "x.x.x"
}
```

```groovy
// build.gradle
plugins {
  id 'io.github.denis-markushin.jooq-codegen' version 'x.x.x'
}
```

When used through the [`spring-service`](../spring-service/README.md) plugin, do not apply it
directly — configure it via `platform { jooq { } }` instead.

---

## Configuration

The plugin exposes a `demaJooq` extension:

```kotlin
demaJooq {
  enabled = true                         // default: true  — master switch; when false, injects and configures nothing
  jooqVersion = "3.19.x"                 // default: bundled jOOQ version
  databaseImage = "postgresql:17.5-alpine" // default — Testcontainers image used to build the codegen JDBC URL

  // Optional override, deep-merged on top of the dema defaults
  configuration {
    generator {
      database {
        includes = "custom_.*"
      }
    }
  }
}
```

### Override semantics

`configuration { }` is layered on top of the built-in defaults — it does not replace them. The two
configuration trees are deep-merged with `MiniJAXB.append`, exactly as the official jOOQ plugin
merges successive `configuration { }` calls:

- Scalar fields set in the override win over the defaults.
- Collections (e.g. `forcedTypes`) are concatenated, override entries first. jOOQ evaluates forced
  types first-match-wins, so an override `forcedType` with an overlapping matcher shadows a default.

---

## Built-in defaults

| Setting             | Value                                                                                        |
|---------------------|----------------------------------------------------------------------------------------------|
| Logging             | `DEBUG`                                                                                      |
| JDBC driver / URL   | `ContainerDatabaseDriver` / `jdbc:tc:<databaseImage>:///test-db`                             |
| Database provider   | `org.dema.jooq.liquibase.LiquibasePostgresTcDatabase`                                        |
| Input schema        | `public`                                                                                     |
| Includes / excludes | `.*` / `databasechangelog`, `databasechangeloglock`                                          |
| Forced types        | `timestamptz` / `timestamp with time zone` → `java.time.LocalDateTime`                       |
| Generate            | no POJOs, no DAOs, no `RecordN`; JetBrains `@Nullable`/`@NotNull` and validation annotations |
| Target directory    | `build/generated/sources/jooq`                                                               |
| Target package      | project `group` with dashes removed                                                          |

---

## How it works

1. On `afterEvaluate` (so `platform { }`/`demaJooq { }` values are settled), if `enabled` is true:
   the runtime and codegen dependencies are injected.
2. The dema defaults and the optional consumer override are built as separate jOOQ configuration
   trees, deep-merged, and marshalled to XML.
3. The `jooqCodegen` task unmarshals that XML and runs `GenerationTool.generate` inside a
   `URLClassLoader` built from the `jooqCodegen` configuration — keeping the codegen classpath
   (JDBC driver, database provider) isolated from the plugin's own classpath.
4. Generated sources land in the `jooq` source set and are wired into `main`/`test` so they compile
   and ship without polluting `main`'s source directories.
