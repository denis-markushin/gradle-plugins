package io.github.denismarkushin.gradle

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import org.gradle.api.Task
import org.gradle.api.internal.TaskInternal
import org.gradle.api.tasks.TaskProvider
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SpotlessPluginTest {

    @TempDir
    lateinit var projectDir: File

    /**
     * bootstrapFile() skips JAR resource loading when the target file already exists.
     * Pre-creating these stubs lets ProjectBuilder tests exercise the plugin without
     * requiring the dot-files to be on the test classpath (they are filtered out by
     * Gradle's default ANT excludes during processResources).
     */
    @BeforeEach
    fun createBootstrapStubs() {
        File(projectDir, ".editorconfig").writeText("# stub for tests\n")
        File(projectDir, ".gitattributes").writeText("# stub for tests\n")
    }

    @Test
    fun `plugin applies spotless which itself registers assemble via base`() {
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.plugins.apply(SpotlessPlugin::class.java)

        assertAll {
            assertThat(project.plugins.hasPlugin("com.diffplug.spotless")).isTrue()
            assertThat(project.tasks.findByName("assemble")).isNotNull()
        }
    }

    @Test
    fun `plugin wires spotlessInstallGitPrePushHook into assemble when base plugin is applied first`() {
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.plugins.apply("base")
        project.plugins.apply(SpotlessPlugin::class.java)

        val assemble = project.tasks.findByName("assemble")
        val hook = project.tasks.findByName("spotlessInstallGitPrePushHook")
        assertAll {
            assertThat(assemble).isNotNull()
            assertThat(hook).isNotNull()
            assertThat(assembleHasDependencyOn(assemble!!, hook!!)).isTrue()
        }
    }

    @Test
    fun `plugin wires spotlessInstallGitPrePushHook into assemble when base plugin is applied after`() {
        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()

        project.plugins.apply(SpotlessPlugin::class.java)
        project.plugins.apply("base")

        val assemble = project.tasks.findByName("assemble")
        val hook = project.tasks.findByName("spotlessInstallGitPrePushHook")
        assertAll {
            assertThat(assemble).isNotNull()
            assertThat(hook).isNotNull()
            assertThat(assembleHasDependencyOn(assemble!!, hook!!)).isTrue()
        }
    }

    @Test
    fun `spotlessInstallGitPrePushHook is skipped when pre-push hook already exists`() {
        val gitHooksDir = File(projectDir, ".git/hooks").apply { mkdirs() }
        File(gitHooksDir, "pre-push").writeText("#!/bin/sh\nexit 0\n")

        val project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        project.plugins.apply("base")
        project.plugins.apply(SpotlessPlugin::class.java)

        // TaskInternal: public Task API exposes onlyIf only as a setter
        val hook = project.tasks.named("spotlessInstallGitPrePushHook").get() as TaskInternal
        val shouldRun = hook.onlyIf.isSatisfiedBy(hook)
        assertThat(shouldRun).isFalse()
    }

    /**
     * Checks whether [parent] has a dependsOn entry that resolves to [dependency].
     * dependsOn entries can be Task instances, TaskProvider<Task>, or Strings —
     * this helper handles all three cases.
     */
    private fun assembleHasDependencyOn(parent: Task, dependency: Task): Boolean =
        parent.dependsOn.any { dep ->
            when (dep) {
                is Task -> dep == dependency
                is TaskProvider<*> -> runCatching { dep.get() == dependency }.getOrDefault(false)
                is String -> dep == dependency.name
                else -> false
            }
        }
}
