package io.github.denismarkushin.gradle.configurator

/** Gradle configurations that receive BOM and platform dependency constraints. */
internal val DEPENDENCY_CONFIGURATIONS = listOf(
    "implementation",
    "api",
    "testImplementation",
    "developmentOnly",
    "kapt",
)
