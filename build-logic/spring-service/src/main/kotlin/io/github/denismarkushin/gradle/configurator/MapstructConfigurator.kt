package io.github.denismarkushin.gradle.configurator

import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import io.github.denismarkushin.gradle.springservice.VersionCatalog
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal fun Project.configureMapstruct() {
    val platformExt = extensions.getByType<DemaPlatformExtension>()

    configurations.named("kapt").configure {
        dependencies.addAllLater(
            provider {
                if (!platformExt.useMapstruct.get()) return@provider emptyList()
                listOf(
                    project.dependencies.create(VersionCatalog.MAPSTRUCT_PROCESSOR_DEP),
                    project.dependencies.create(VersionCatalog.MAPSTRUCT_SPRING_EXTENSIONS_DEP),
                )
            },
        )
    }

    configurations.named("implementation").configure {
        dependencies.addAllLater(
            provider {
                if (!platformExt.useMapstruct.get()) return@provider emptyList()
                listOf(
                    project.dependencies.create(VersionCatalog.MAPSTRUCT_CORE_DEP),
                    project.dependencies.create(VersionCatalog.MAPSTRUCT_SPRING_ANNOTATIONS_DEP),
                )
            },
        )
    }

    plugins.withId("org.jetbrains.kotlin.kapt") {
        extensions.configure<KaptExtension> {
            arguments {
                arg("mapstruct.defaultComponentModel", "spring")
                arg("mapstruct.unmappedTargetPolicy", "ERROR")
            }
        }
    }
}
