package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.configurator.configurePublishing
import io.github.denismarkushin.gradle.configurator.configureRepositories
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.apply

class LibraryPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(KotlinPlugin::class)
        plugins.apply(JavaLibraryPlugin::class)
        plugins.apply("org.jetbrains.kotlin.plugin.spring")

        configureRepositories()
        configurePublishing()
    }
}
