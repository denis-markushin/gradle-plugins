package io.github.denismarkushin.gradle

import com.akuleshov7.vercraft.VercraftPlugin
import io.github.denismarkushin.gradle.configurator.configureJcabi
import io.github.denismarkushin.gradle.configurator.configureJooq
import io.github.denismarkushin.gradle.configurator.configureKapt
import io.github.denismarkushin.gradle.configurator.configureMapstruct
import io.github.denismarkushin.gradle.configurator.configureNetflixDgsFramework
import io.github.denismarkushin.gradle.configurator.configureSpringBootBuildInfo
import io.github.denismarkushin.gradle.configurator.configureSpringBootRequiredDependencies
import io.github.denismarkushin.gradle.configurator.configureSpringCloudDependencies
import io.github.denismarkushin.gradle.configurator.configureTestTask
import io.github.denismarkushin.gradle.extension.DemaPlatformExtension.Companion.thePlatform
import io.github.denismarkushin.gradle.util.bootstrapFile
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.filter
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

private const val DOCKERIGNORE_RESOURCE = "/org/dema/.dockerignore"
private const val DOCKERFILE_RESOURCE = "/org/dema/Dockerfile"
private const val SERVICE_GITLAB_CI_RESOURCE = "/org/dema/.gitlab-ci.yml"

class SpringBootServicePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(VercraftPlugin::class)
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

        bootstrapFile(DOCKERIGNORE_RESOURCE, ".dockerignore")
        bootstrapFile(DOCKERFILE_RESOURCE, "Dockerfile")
        bootstrapFile(SERVICE_GITLAB_CI_RESOURCE, ".gitlab-ci.yml")

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
     */
    private fun Project.configureProcessResourcesTokenFiltering() {
        tasks.withType<ProcessResources> {
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
