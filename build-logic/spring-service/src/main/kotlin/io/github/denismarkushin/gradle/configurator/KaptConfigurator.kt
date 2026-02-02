package io.github.denismarkushin.gradle.configurator

import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureKapt() {
    extensions.extraProperties["kapt.use.k2"] = "true"
    plugins.apply("kotlin-kapt")

    val ext = extensions.getByType<DemaPlatformExtension>()

    configurations.named("kapt").configure {
        dependencies.addAllLater(
            provider {
                if (!ext.useKapt.get() && !ext.useMapstruct.get()) return@provider emptyList()
                listOf(project.dependencies.create("org.springframework.boot:spring-boot-configuration-processor"))
            },
        )
    }

    tasks.matching { it.name.startsWith("kapt") }.configureEach {
        onlyIf {
            ext.useKapt.get() || ext.useMapstruct.get()
        }
    }
}
