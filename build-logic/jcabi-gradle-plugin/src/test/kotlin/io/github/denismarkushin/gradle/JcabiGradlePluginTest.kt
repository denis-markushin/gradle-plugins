package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import io.freefair.gradle.plugins.aspectj.AjcAction
import io.freefair.gradle.plugins.aspectj.AspectJPostCompileWeavingPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class JcabiGradlePluginTest {

    private fun buildProject() = ProjectBuilder.builder().build().also { project ->
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(JcabiGradlePlugin::class.java)
    }

    @Test
    fun `plugin applies AspectJ post-compile-weaving plugin`() {
        val project = buildProject()

        assertThat(project.plugins.hasPlugin(AspectJPostCompileWeavingPlugin::class.java)).isTrue()
    }

    @Test
    fun `plugin adds jcabi-aspects to aspect configuration`() {
        val project = buildProject()

        val aspectDeps = project.configurations.getByName("aspect").dependencies
        val jcabiDep = aspectDeps.find { it.group == "com.jcabi" && it.name == "jcabi-aspects" }
        assertThat(jcabiDep).isNotNull()
    }

    @Test
    fun `plugin adds Xlint ignore compiler arg to compile tasks`() {
        val project = buildProject()

        val compileTasks = project.tasks.filter { it.name.startsWith("compile") }
        compileTasks.forEach { task ->
            val ajcAction = task.extensions.findByType(AjcAction::class.java)
            if (ajcAction != null) {
                assertThat(ajcAction.options.compilerArgs).contains("-Xlint:ignore")
            }
        }
    }
}
