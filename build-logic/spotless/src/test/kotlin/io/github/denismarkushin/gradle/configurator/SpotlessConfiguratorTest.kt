package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import io.github.denismarkushin.gradle.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SpotlessConfiguratorTest {

    private fun buildProject(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File): Project =
        ProjectBuilder.builder().withProjectDir(projectDir).build().also { project ->
            project.plugins.apply(SpotlessPlugin::class.java)
        }

    @Test
    fun `applies spotless plugin`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        assertThat(project.plugins.hasPlugin("com.diffplug.spotless")).isTrue()
    }

    @Test
    fun `configures spotless line endings`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        val extension = project.extensions.findByType(SpotlessExtension::class.java)
        assertThat(extension).isNotNull()
        assertThat(extension!!.lineEndings).isEqualTo(LineEnding.GIT_ATTRIBUTES_FAST_ALLSAME)
    }

    @Test
    fun `bootstraps editorconfig into rootDir`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        buildProject(projectDir)
        val editorConfig = File(projectDir, ".editorconfig")
        assertThat(editorConfig.exists()).isTrue()
    }

    @Test
    fun `bootstraps gitattributes into rootDir`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        buildProject(projectDir)
        val gitAttributes = File(projectDir, ".gitattributes")
        assertThat(gitAttributes.exists()).isTrue()
    }

    @Test
    fun `does not overwrite existing editorconfig`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val existing = File(projectDir, ".editorconfig").apply { writeText("# user content") }
        buildProject(projectDir)
        assertThat(existing.readText()).isEqualTo("# user content")
    }

    @Test
    fun `does not overwrite existing gitattributes`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val existing = File(projectDir, ".gitattributes").apply { writeText("* text=auto") }
        buildProject(projectDir)
        assertThat(existing.readText()).isEqualTo("* text=auto")
    }

    @Test
    fun `registers spotlessInstallGitPrePushHook task`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        assertThat(project.tasks.findByName("spotlessInstallGitPrePushHook")).isNotNull()
    }

    @Test
    fun `wires assemble to depend on spotlessInstallGitPrePushHook when base applied`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        project.plugins.apply("base")

        val assemble = project.tasks.getByName("assemble")
        val deps = assemble.taskDependencies.getDependencies(assemble).map { it.name }
        assertThat(deps).contains("spotlessInstallGitPrePushHook")
    }

    @Test
    fun `spotlessInstallGitPrePushHook is skipped when pre-push hook exists`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        File(projectDir, ".git/hooks").mkdirs()
        File(projectDir, ".git/hooks/pre-push").writeText("#!/bin/sh\n")

        val task = project.tasks.getByName("spotlessInstallGitPrePushHook") as org.gradle.api.internal.TaskInternal
        assertThat(task.onlyIf.isSatisfiedBy(task)).isEqualTo(false)
    }

    @Test
    fun `spotlessInstallGitPrePushHook runs when no pre-push hook present`(@TempDir(cleanup = CleanupMode.NEVER) projectDir: File) {
        val project = buildProject(projectDir)
        val task = project.tasks.getByName("spotlessInstallGitPrePushHook") as org.gradle.api.internal.TaskInternal
        assertThat(task.onlyIf.isSatisfiedBy(task)).isTrue()
    }
}
