package io.github.denismarkushin.gradle.configurator

import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.withType

internal fun Project.configurePublishing() {
    plugins.apply("com.vanniktech.maven.publish")

    tasks.withType(AbstractPublishToMaven::class).configureEach {
        doLast {
            publication.apply {
                logger.lifecycle("Published $groupId:$artifactId:$version artifact")
            }
        }
    }
}
