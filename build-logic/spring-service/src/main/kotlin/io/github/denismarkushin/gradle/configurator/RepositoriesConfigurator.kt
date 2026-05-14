package io.github.denismarkushin.gradle.configurator

import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

internal fun Project.configureRepositories() {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
