import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

fun Project.configurePublishing() {
    extensions.configure<MavenPublishBaseExtension> {
        coordinates(project.group.toString(), project.name, project.version.toString())

        pom {
            name.set(project.name)
            description.set(project.description)
            inceptionYear.set("2025")
            url.set("https://github.com/denis-markushin/common-libs/")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("denis-markushin")
                    name.set("Denis Markushin")
                    url.set("https://github.com/denis-markushin/")
                }
            }
            scm {
                url.set("https://github.com/denis-markushin/common-libs/")
                connection.set("scm:git:git://github.com:denis-markushin/common-libs.git")
                developerConnection.set("scm:git:ssh://git@github.com:denis-markushin/common-libs.git")
            }
        }
    }

    tasks.withType<AbstractPublishToMaven> {
        doLast {
            publication.apply {
                logger.lifecycle("Published $groupId:$artifactId:$version artifact")
            }
        }
    }
}