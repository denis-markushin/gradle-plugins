package org.demalab.gradle.configurator

import org.gradle.api.Project
import org.gradle.kotlin.dsl.repositories

internal fun Project.configureRepositories() {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
