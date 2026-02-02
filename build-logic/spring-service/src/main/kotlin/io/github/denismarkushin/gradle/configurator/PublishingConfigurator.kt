package io.github.denismarkushin.gradle.configurator

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

internal fun Project.configurePublishing() {
    plugins.apply("maven-publish")

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components.getByName("java"))
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }

    tasks.withType(AbstractPublishToMaven::class).configureEach {
        doLast {
            publication.apply {
                logger.lifecycle("Published $groupId:$artifactId:$version artifact")
            }
        }
    }
}
