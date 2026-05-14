package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.configurator.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies Spotless with ktlint to the target project.
 * Bootstraps `.editorconfig` into rootDir if one is not already present.
 *
 * Apply at the root project level in multi-module builds so that glob patterns
 * cover all submodule sources.
 */
class SpotlessPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        configureSpotless()
    }
}
