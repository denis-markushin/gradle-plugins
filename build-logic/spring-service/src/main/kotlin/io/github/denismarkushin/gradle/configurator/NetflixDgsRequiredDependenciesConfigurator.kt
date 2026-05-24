package io.github.denismarkushin.gradle.configurator

import com.netflix.graphql.dgs.codegen.gradle.CodegenPlugin
import com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask
import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import io.github.denismarkushin.gradle.springservice.VersionCatalog.DEMA_GRAPHQL_SCALARS_DEP
import io.github.denismarkushin.gradle.springservice.VersionCatalog.DEMA_GRAPHQL_STARTER_DEP
import io.github.denismarkushin.gradle.springservice.VersionCatalog.NETFLIX_DGS_BOM_DEP
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun Project.configureNetflixDgsFramework() {
    val platformExt = extensions.getByType<DemaPlatformExtension>()

    configurations.matching { it.name in DEPENDENCY_CONFIGURATIONS }.configureEach {
        dependencies.addAllLater(
            provider {
                if (!platformExt.spring.netflixDgs.useNetflixDgs.get()) return@provider emptyList()
                listOf(
                    project.dependencies.create(project.dependencies.platform(NETFLIX_DGS_BOM_DEP)),
                )
            },
        )
    }

    afterEvaluate {
        val dgsExt = platformExt.spring.netflixDgs
        if (!dgsExt.useNetflixDgs.get()) return@afterEvaluate

        plugins.apply(CodegenPlugin::class)

        // Auto-wire dema common GraphQL schema starters into dgsCodegen so codegen sees
        // PageInfo / Node / MutationResult / error types / scalars from the shared starter.
        // Services no longer need to declare these in their own `dependencies { }` blocks.
        dependencies.add("dgsCodegen", DEMA_GRAPHQL_STARTER_DEP)
        dependencies.add("dgsCodegen", DEMA_GRAPHQL_SCALARS_DEP)

        val generator = dgsExt.generator
        tasks.withType<GenerateJavaTask>().configureEach {
            packageName = rootProject.group.toString().replace("-", "")
            generateDocs = generator.generateDocs.get()
            generateClient = generator.generateClient.get()
            generateBoxedTypes = generator.generateBoxedTypes.get()
            snakeCaseConstantNames = generator.snakeCaseConstantNames.get()
            addGeneratedAnnotation = generator.addGeneratedAnnotation.get()
            generateKotlinNullableClasses = generator.generateKotlinNullableClasses.get()
            generateKotlinClosureProjections = generator.generateKotlinClosureProjections.get()
            typeMapping = generator.typeMapping.get()
        }
    }
}
