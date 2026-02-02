package io.github.denismarkushin.gradle.configurator

import org.gradle.api.Project
import org.springframework.boot.gradle.plugin.SpringBootPlugin

internal fun Project.configureSpringBootRequiredDependencies() {
    configurations.matching { it.name in DEPENDENCY_CONFIGURATIONS }.configureEach {
        dependencies.addAllLater(
            provider {
                listOf(project.dependencies.create(project.dependencies.platform(SpringBootPlugin.BOM_COORDINATES)))
            },
        )
    }
    configurations.named("developmentOnly") {
        dependencies.addAllLater(
            provider {
                listOf(
                    project.dependencies.create("org.springframework.boot:spring-boot-devtools"),
                    project.dependencies.create("org.springframework.boot:spring-boot-docker-compose"),
                )
            },
        )
    }
}
