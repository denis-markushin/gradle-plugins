plugins {
    alias(libs.plugins.buildconfig)
}

buildConfig {
    className("VersionCatalog")
    packageName("io.github.denismarkushin.gradle.jcabi")
    useKotlinOutput { internalVisibility = true }

    buildConfigField("JCABI_ASPECTS_DEP", libs.jcabiAspects.get().toString())
}

val testPlugins = configurations.create("testPlugins") {
    extendsFrom(configurations.runtimeClasspath.get())
}

dependencies {
    implementation(plugin(rootProject.libs.plugins.postCompileWeaving))
    testPlugins(plugin(rootProject.libs.plugins.kotlin.jvm))
}

tasks.pluginUnderTestMetadata {
    pluginClasspath.setFrom(sourceSets.main.map { it.output }, testPlugins)
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("jcabiGradlePlugin") {
            id = "io.github.denis-markushin.jcabi-gradle-plugin"
            implementationClass = "io.github.denismarkushin.gradle.JcabiGradlePlugin"
            displayName = "Jcabi plugin for gradle"
            description = "The plugin configures post-compile-weaving and adds required jcabi dependencies"
            tags.set(listOf("jcabi", "aspect"))
        }
    }
}
