package io.github.denismarkushin.gradle.configurator

import com.gorylenko.GitPropertiesPlugin
import com.gorylenko.GitPropertiesPluginExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.springframework.boot.gradle.dsl.SpringBootExtension

internal fun Project.configureSpringBootBuildInfo() {
    plugins.apply(GitPropertiesPlugin::class)

    extensions.configure<SpringBootExtension> {
        buildInfo()
    }

    extensions.configure<GitPropertiesPluginExtension> {
        branch = findProperty("ci.branch")?.toString()
            ?: System.getenv("CI_COMMIT_REF_NAME")
                ?: "unknown"
    }
}
