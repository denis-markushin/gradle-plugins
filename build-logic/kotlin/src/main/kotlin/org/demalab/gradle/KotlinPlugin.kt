package org.demalab.gradle

import org.demalab.gradle.configurator.configureCompilation
import org.demalab.gradle.configurator.configureKover
import org.demalab.gradle.configurator.configureRepositories
import org.demalab.gradle.kotlinlibrary.VersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("org.jetbrains.kotlin.jvm")

        configureRepositories()
        configureCompilation(VersionCatalog.JAVA_VERSION.toString())
        configureKover()
    }
}
