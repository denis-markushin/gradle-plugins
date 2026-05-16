package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.github.denismarkushin.gradle.kotlinlibrary.VersionCatalog
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class KotlinPluginTest {

    private fun buildProject() = ProjectBuilder.builder().build().also { project ->
        project.plugins.apply(KotlinPlugin::class.java)
    }

    @Test
    fun `plugin applies kotlin-jvm plugin`() {
        val project = buildProject()

        assertThat(project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")).isTrue()
    }

    @Test
    fun `plugin configures mavenCentral repository`() {
        val project = buildProject()

        val repoNames = project.repositories.map { it.name }
        assertThat(repoNames.contains("MavenRepo")).isTrue()
    }

    @Test
    fun `plugin configures mavenLocal repository`() {
        val project = buildProject()

        val repoNames = project.repositories.map { it.name }
        assertThat(repoNames.contains("MavenLocal")).isTrue()
    }

    @Test
    fun `plugin configures Java source compatibility`() {
        val project = buildProject()

        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        assertThat(javaExtension.sourceCompatibility.toString()).isEqualTo(VersionCatalog.JAVA_VERSION.toString())
    }

    @Test
    fun `plugin configures Java target compatibility`() {
        val project = buildProject()

        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        assertThat(javaExtension.targetCompatibility.toString()).isEqualTo(VersionCatalog.JAVA_VERSION.toString())
    }
}
