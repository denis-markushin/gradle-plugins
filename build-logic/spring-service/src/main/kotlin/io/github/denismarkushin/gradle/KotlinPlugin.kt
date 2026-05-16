package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.configurator.configureCompilation
import io.github.denismarkushin.gradle.configurator.configureRepositories
import io.github.denismarkushin.gradle.springservice.VersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

open class KotlinPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("org.jetbrains.kotlin.jvm")

        configureRepositories()
        configureCompilation(VersionCatalog.JAVA_VERSION.toString())
    }
}
