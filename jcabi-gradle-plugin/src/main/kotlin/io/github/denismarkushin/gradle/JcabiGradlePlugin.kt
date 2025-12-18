package io.github.denismarkushin.gradle

import io.freefair.gradle.plugins.aspectj.AjcAction
import io.freefair.gradle.plugins.aspectj.AspectJPostCompileWeavingPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class JcabiGradlePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(AspectJPostCompileWeavingPlugin::class.java)
        dependencies.add("aspect", VersionCatalog.JCABI_ASPECTS_DEP)
        addXlintIgnoreForCompileTasks()
    }
}

private fun Project.addXlintIgnoreForCompileTasks() {
    tasks.named { it.startsWith("compile") }.configureEach {
        it.extensions.findByType(AjcAction::class.java)?.apply {
            this.options.compilerArgs.add("-Xlint:ignore")
        }
    }
}
