package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isTrue
import io.github.denismarkushin.gradle.KotlinPlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class KoverConfiguratorTest {

    private fun evaluated(): Project {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(KotlinPlugin::class.java)
        (project as ProjectInternal).evaluate()
        return project
    }

    private fun checkDependencies(project: Project): List<String> {
        val check = project.tasks.getByName("check")
        return check.taskDependencies.getDependencies(check).map { it.name }
    }

    @Test
    fun `plugin applies kover`() {
        val project = evaluated()
        assertThat(project.plugins.hasPlugin("org.jetbrains.kotlinx.kover"), "a project without kover measures no coverage").isTrue()
    }

    @Test
    fun `check writes the xml report`() {
        assertThat(checkDependencies(evaluated()), "CI runs check and finds no XML report for GitLab").contains("koverXmlReport")
    }

    @Test
    fun `check logs the coverage percentage`() {
        assertThat(checkDependencies(evaluated()), "CI runs check and finds no coverage line to parse").contains("koverLog")
    }

    @Test
    fun `generated classes are excluded by annotation`() {
        val excludes = evaluated().extensions.getByType(KoverProjectExtension::class.java).reports.filters.excludes
        assertThat(excludes.annotatedBy.get(), "DGS and jOOQ generated classes count against coverage").contains("*Generated")
    }

    @Test
    fun `mapstruct implementations are excluded by name`() {
        val excludes = evaluated().extensions.getByType(KoverProjectExtension::class.java).reports.filters.excludes
        assertThat(excludes.classes.get(), "MapStruct carries a SOURCE-retention annotation, so its implementations count against coverage").contains("*Impl")
    }

    @Test
    fun `aspectj closures are excluded by name`() {
        val excludes = evaluated().extensions.getByType(KoverProjectExtension::class.java).reports.filters.excludes
        assertThat(excludes.classes.get(), "AspectJ weaving emits synthetic closure classes that count against coverage").contains("*\$AjcClosure*")
    }
}
