# jcabi-gradle-plugin

[![Build](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/denis-markushin/gradle-plugins/actions/workflows/gradle-ci.yml)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.denis-markushin.jcabi-gradle-plugin)](https://plugins.gradle.org/plugin/io.github.denis-markushin.jcabi-gradle-plugin)

Gradle plugin that wires [jcabi-aspects](https://github.com/jcabi/jcabi-aspects) into your build by enabling post-compile weaving and adding
the required dependencies automatically.

## Requirements

- Java 21
- Gradle 8+

## What the plugin does

- Applies the FreeFair AspectJ Post-Compile Weaving plugin to hook AspectJ into the Java/Kotlin compilation tasks.
- Adds `com.jcabi:jcabi-aspects` to the `aspect` configuration so jcabi annotations are woven into compiled classes.
- Configures all `compile*` tasks that support AspectJ (`AjcAction`) to use `-Xlint:ignore` to silence AspectJ lint warnings during weaving.

## Installation

```kotlin
plugins {
    id("io.github.denis-markushin.jcabi-gradle-plugin") version "x.x.x"
}
```

```groovy
plugins {
    id 'io.github.denis-markushin.jcabi-gradle-plugin' version 'x.x.x'
}
```

## Usage

Once applied, you can start using jcabi annotations such as `@Loggable`, `@RetryOnFailure`, or `@Parallel` in your code.
The plugin will weave the necessary aspects during compilation without any additional setup.

### Overriding the jcabi-aspects version

The plugin adds `com.jcabi:jcabi-aspects` using the version bundled with the plugin. If you need a different version, declare it explicitly
on the `aspect` configuration to override the default:

```kotlin
dependencies {
    aspect("com.jcabi:jcabi-aspects:0.27.0")
}
```

### Notes on weaving

- Weaving is applied to any task whose name starts with `compile` (for example, `compileJava` and `compileKotlin`) as long as it supports
  AspectJ weaving.
- The `-Xlint:ignore` flag is set on supported tasks to reduce noise from AspectJ lint warnings.

## Further reading

Refer to the [jcabi-aspects documentation](https://aspects.jcabi.com/index.html) for available annotations and behavior, and to
the [FreeFair AspectJ plugin docs](https://plugins.gradle.org/plugin/io.freefair.aspectj.post-compile-weaving) for details on how weaving is
performed.
