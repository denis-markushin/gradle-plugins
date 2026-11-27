package org.demalab.gradle

import org.apache.tools.ant.filters.ReplaceTokens
import org.demalab.gradle.configurator.configureJcabi
import org.demalab.gradle.configurator.configureJooq
import org.demalab.gradle.configurator.configureKapt
import org.demalab.gradle.configurator.configureMapstruct
import org.demalab.gradle.configurator.configureNetflixDgsFramework
import org.demalab.gradle.configurator.configureSpringBootBuildInfo
import org.demalab.gradle.configurator.configureSpringBootRequiredDependencies
import org.demalab.gradle.configurator.configureSpringCloudDependencies
import org.demalab.gradle.configurator.configureTestTask
import org.demalab.gradle.configurator.configureVersioning
import org.demalab.gradle.extension.DemaPlatformExtension.Companion.thePlatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.filter
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class SpringBootServicePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        configureVersioning()
        plugins.apply(KotlinPlugin::class)
        plugins.apply(SpotlessPlugin::class)
        plugins.apply("org.jetbrains.kotlin.plugin.spring")
        plugins.apply("org.springframework.boot")

        thePlatform()

        configureSpringBootRequiredDependencies()
        configureSpringCloudDependencies()
        configureNetflixDgsFramework()
        configureSpringBootBuildInfo()
        configureTestTask()
        configureKapt()
        configureMapstruct() // depends on configureKapt()
        configureJooq()
        configureJcabi()
        configureProcessResourcesTokenFiltering()

        // disable *-sources.jar and *-plain.jar
        tasks.matching { it.name == "jar" || it.name == "sourcesJar" }.configureEach {
            enabled = false
        }
    }

    /**
     * Configures the `processResources` task to substitute tokens using `rootProject.properties`
     * so that they can be referenced from `application.yml`.
     *
     * Example:
     * ```yaml
     * spring:
     *   application:
     *     name: "@project.name@"
     * ```
     *
     * `Project.getProperties()` is deprecated and removed in Gradle 10, but no full-map replacement
     * exists yet: `gradleProperty()` resolves a single key. Revisit on the Gradle 10 bump.
     */
    private fun Project.configureProcessResourcesTokenFiltering() {
        tasks.withType<ProcessResources> {
            @Suppress("DEPRECATION")
            val substitutionTokens =
                rootProject.properties
                    .map { (key, value) -> "project.$key" to (value?.toString() ?: "unknown") }
                    .toMap()
            filesMatching(listOf("**/*.yml", "**/*.yaml", "**/*.properties")) {
                filter<ReplaceTokens>("tokens" to substitutionTokens)
            }
        }
    }
}
