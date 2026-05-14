package io.github.denismarkushin.gradle.configurator

import io.github.denismarkushin.gradle.VersionCatalog.SPRING_CLOUD_BOM_DEP
import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureSpringCloudDependencies() {
    val platformExt = extensions.getByType<DemaPlatformExtension>()

    configurations.matching { it.name in DEPENDENCY_CONFIGURATIONS }.configureEach {
        dependencies.addAllLater(
            provider {
                if (!platformExt.spring.useCloud.get()) return@provider emptyList()
                listOf(
                    project.dependencies.create(project.dependencies.platform(SPRING_CLOUD_BOM_DEP)),
                )
            },
        )
    }
}
