package io.github.denismarkushin.gradle

import io.freefair.gradle.plugins.aspectj.AjcAction
import io.freefair.gradle.plugins.aspectj.AspectJPostCompileWeavingPlugin
import io.github.denismarkushin.gradle.jcabi.VersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class JcabiGradlePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply(AspectJPostCompileWeavingPlugin::class.java)
        dependencies.add("aspect", VersionCatalog.JCABI_ASPECTS_DEP)
        dependencies.constraints.add("aspect", VersionCatalog.ASPECTJ_RT_DEP) {
            because("ajc inferred from an older aspectjrt rejects recent java targets")
        }
        configureWeavingCompileTasks()
    }
}

private fun Project.configureWeavingCompileTasks() {
    tasks.named { it.startsWith("compile") }.configureEach {
        val ajc = extensions.findByType(AjcAction::class.java) ?: return@configureEach
        ajc.options.compilerArgs.add("-Xlint:ignore")
        doFirst("deleteAjcClosures") {
            ajc.additionalInpath.asFileTree.matching { include("**/*\$AjcClosure*.class") }.forEach { it.delete() }
        }
    }
}
