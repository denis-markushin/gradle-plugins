package org.demalab.gradle.configurator

import org.demalab.gradle.JcabiGradlePlugin
import org.demalab.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureJcabi() = afterEvaluate {
    if (!extensions.getByType<DemaPlatformExtension>().useJcabi.get()) return@afterEvaluate
    plugins.apply(JcabiGradlePlugin::class)
}
