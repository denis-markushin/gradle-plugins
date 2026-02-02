package io.github.denismarkushin.gradle.util

import org.gradle.api.Project

private object PluginResources

internal fun Project.bootstrapFile(resourcePath: String, fileName: String) {
    val target = rootProject.file(fileName)
    if (target.exists()) return
    val stream = PluginResources::class.java.getResourceAsStream(resourcePath)
        ?: error("Plugin resource '$resourcePath' not found in JAR")
    target.parentFile?.mkdirs()
    stream.use { it.copyTo(target.outputStream()) }
    logger.lifecycle(
        "[spotless-plugin] Bootstrapped $fileName → ${target.absolutePath}\n" +
            "  This file can be committed and customized. It will not be overwritten.",
    )
}
