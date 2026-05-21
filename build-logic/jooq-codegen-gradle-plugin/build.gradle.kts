plugins {
    alias(libs.plugins.buildconfig)
}

buildConfig {
    className("VersionCatalog")
    packageName("io.github.denismarkushin.gradle.jooq")
    useKotlinOutput { internalVisibility = true }

    buildConfigField("JOOQ_VERSION", libs.versions.jooq.get())
    buildConfigField("JOOQ_LIQUIBASE_TC_DEP", libs.dema.jooq.liquibase.tc.get().toString())
}

dependencies {
    implementation(plugin(libs.plugins.jooq.codegen))
    implementation(libs.jooq.codegen)
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("jooqCodegenGradlePlugin") {
            id = "io.github.denis-markushin.jooq-codegen"
            implementationClass = "io.github.denismarkushin.gradle.JooqCodegenPlugin"
            displayName = "jOOQ codegen convention plugin"
            description = "Generates jOOQ sources with opinionated dema defaults into a dedicated " +
                "source set, and injects the jOOQ runtime and codegen dependencies."
            tags.set(listOf("jooq", "codegen", "convention-plugin", "kotlin"))
        }
    }
}
