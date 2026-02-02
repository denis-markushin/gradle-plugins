package io.github.denismarkushin.gradle.configurator

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureCompilation(
    javaVersion: String,
) {
    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(javaVersion.toInt())
    }

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
            freeCompilerArgs.addAll(
                "-Xjsr305=strict", // Enables support for @Nullable and @Nonnull when using Java-based libraries
                "-Xemit-jvm-type-annotations", // Support for new @Target annotations from JDK 1.8+ (e.g., List<@NotEmpty String>)
                "-Xannotation-default-target=param-property", // Change defaulting rule for annotations (see KT-73255)
            )
            javaParameters.set(true) // Generates metadata for method parameter names
            allWarningsAsErrors.set(true)
        }
    }
}
