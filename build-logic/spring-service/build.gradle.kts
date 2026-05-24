plugins {
    alias(libs.plugins.buildconfig)
}

buildConfig {
    className("VersionCatalog")
    packageName("io.github.denismarkushin.gradle.springservice")
    // Spring
    buildConfigField("SPRING_CLOUD_BOM_DEP", libs.cloud.dependencies.get().toString())
    buildConfigField("NETFLIX_DGS_BOM_DEP", libs.netflix.dgs.dependencies.get().toString())
    // Mapstruct
    buildConfigField("MAPSTRUCT_CORE_DEP", libs.mapstruct.core.get().toString())
    buildConfigField("MAPSTRUCT_PROCESSOR_DEP", libs.mapstruct.processor.get().toString())
    buildConfigField("MAPSTRUCT_SPRING_ANNOTATIONS_DEP", libs.mapstruct.spring.annotations.get().toString())
    buildConfigField("MAPSTRUCT_SPRING_EXTENSIONS_DEP", libs.mapstruct.spring.extensions.get().toString())
    // Dema
    buildConfigField("DEMA_GRAPHQL_STARTER_DEP", libs.dema.graphql.starter.get().toString())
    buildConfigField("DEMA_GRAPHQL_SCALARS_DEP", libs.dema.graphql.scalars.get().toString())
    // Testing
    buildConfigField("JUNIT_LAUNCHER_DEP", libs.junit.launcher.get().toString())
    buildConfigField("SPRING_BOOT_TEST_DEP", libs.spring.boot.test.get().toString())
    buildConfigField("ASSERTK_DEP", libs.assertk.get().toString())
    buildConfigField("SPRINGMOCK_DEP", libs.springmock.get().toString())
    buildConfigField("KOTEST_DEP", libs.kotest.get().toString())
    buildConfigField("AWAITILITY_DEP", libs.awaitility.get().toString())
}

dependencies {
    implementation(project(":kotlin"))
    implementation(project(":spotless"))
    implementation(project(":jcabi-gradle-plugin"))
    implementation(project(":jooq-codegen-gradle-plugin"))
    implementation(plugin(libs.plugins.kotlin.spring))
    implementation(plugin(libs.plugins.jooq.codegen))
    implementation(plugin(libs.plugins.spring.boot))
    implementation(plugin(libs.plugins.git.properties))
    implementation(plugin(libs.plugins.vercraft))
    implementation(plugin(libs.plugins.netflix.dgsCodegen))
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("spring-service") {
            id = "io.github.denis-markushin.spring-service"
            displayName = "Spring Boot Service Convention Plugin"
            description = """
                Opinionated convention plugin for production-ready Spring Boot microservices.
                Pre-configures Kotlin, Spring Cloud, Netflix DGS (GraphQL), MapStruct,
                Spotless code formatting, testing stack (JUnit 5, assertk, awaitility),
                and semantic versioning — so you can focus on business logic.
            """.trimIndent()
            implementationClass = "io.github.denismarkushin.gradle.SpringBootServicePlugin"
            tags.set(
                listOf(
                    "spring-boot",
                    "kotlin",
                    "convention-plugin",
                    "microservice",
                    "spring-cloud",
                    "graphql",
                    "dgs",
                    "mapstruct",
                    "spotless",
                    "ci-cd",
                ),
            )
        }
    }
}
