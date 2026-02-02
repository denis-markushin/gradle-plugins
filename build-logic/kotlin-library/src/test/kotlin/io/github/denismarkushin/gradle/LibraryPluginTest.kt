package io.github.denismarkushin.gradle

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class LibraryPluginTest {

    private fun buildProject() = ProjectBuilder.builder().build().also { project ->
        project.plugins.apply(LibraryPlugin::class.java)
    }

    @Test
    fun `plugin applies kotlin-jvm plugin`() {
        val project = buildProject()
        assertThat(project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")).isTrue()
    }

    @Test
    fun `plugin applies java-library plugin`() {
        val project = buildProject()
        assertThat(project.plugins.hasPlugin("java-library")).isTrue()
    }

    @Test
    fun `plugin applies maven-publish plugin`() {
        val project = buildProject()
        assertThat(project.plugins.hasPlugin("maven-publish")).isTrue()
    }

    @Test
    fun `plugin applies vanniktech maven publish plugin`() {
        val project = buildProject()
        assertThat(project.plugins.hasPlugin("com.vanniktech.maven.publish")).isTrue()
    }

    @Test
    fun `plugin configures mavenCentral and mavenLocal repositories`() {
        val project = buildProject()

        val repoNames = project.repositories.map { it.name }
        assertAll {
            assertThat(repoNames.contains("MavenRepo")).isTrue()
            assertThat(repoNames.contains("MavenLocal")).isTrue()
        }
    }
}
