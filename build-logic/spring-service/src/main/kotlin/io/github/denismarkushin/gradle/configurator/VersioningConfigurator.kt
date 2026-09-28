package io.github.denismarkushin.gradle.configurator

import com.akuleshov7.vercraft.DEFAULT_MAIN_BRANCH
import com.akuleshov7.vercraft.VercraftPlugin
import io.github.denismarkushin.gradle.versioning.RepositoryInspection
import io.github.denismarkushin.gradle.versioning.Versioning
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra

private const val SNAPSHOT = "0.0.0-SNAPSHOT"

/**
 * Applies Vercraft when it can compute a version, otherwise sets a snapshot version.
 *
 * The main branch found by [RepositoryInspection] reaches Vercraft through the
 * `defaultMainBranch` extra property, which Vercraft reads with `findProperty`.
 */
internal fun Project.configureVersioning() {
    val inspection = RepositoryInspection(rootDir, { findProperty(it) }, { providers.environmentVariable(it).orNull })
    when (val versioning = inspection.versioning()) {
        is Versioning.Snapshot -> {
            logger.warn("Vercraft skipped and version set to $SNAPSHOT because ${versioning.reason}")
            version = SNAPSHOT
        }
        is Versioning.Vercraft -> {
            versioning.branch?.let {
                logger.info("Vercraft main branch set to $it for repository at $rootDir")
                extra[DEFAULT_MAIN_BRANCH] = it
            }
            plugins.apply(VercraftPlugin::class)
        }
    }
}
