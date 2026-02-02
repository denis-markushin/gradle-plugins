package io.github.denismarkushin.gradle.configurator

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import io.github.denismarkushin.gradle.util.bootstrapFile
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

private const val EDITORCONFIG_RESOURCE = "/org/dema/.editorconfig"
private const val GITATTRIBUTES_RESOURCE = "/org/dema/.gitattributes"

/**
 * Applies the Spotless plugin and configures ktlint for Kotlin sources.
 *
 * Bootstraps `.editorconfig` into rootDir if absent (never overwritten).
 * The editorconfig is resolved eagerly at configuration time because ktlint
 * reads it before any Gradle task executes.
 */
internal fun Project.configureSpotless() {
    bootstrapFile(EDITORCONFIG_RESOURCE, ".editorconfig")
    bootstrapFile(GITATTRIBUTES_RESOURCE, ".gitattributes")

    plugins.apply("com.diffplug.spotless")

    extensions.configure<SpotlessExtension> {
        lineEndings = LineEnding.GIT_ATTRIBUTES_FAST_ALLSAME

        val editorConfigFile = rootProject.file(".editorconfig")
        kotlin {
            target("**/src/**/*.kt")
            ktlint().setEditorConfigPath(editorConfigFile.path)
            toggleOffOn()
        }

        kotlinGradle {
            target("*.gradle.kts")
            ktlint().setEditorConfigPath(editorConfigFile.path)
            toggleOffOn()
        }
    }

    tasks.matching { it.name == "assemble" }.configureEach {
        dependsOn(tasks.named("spotlessInstallGitPrePushHook"))
    }
    tasks.named("spotlessInstallGitPrePushHook").configure {
        onlyIf { !rootProject.file(".git/hooks/pre-push").exists() }
    }
}
