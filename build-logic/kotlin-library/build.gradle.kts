plugins {
    alias(libs.plugins.buildconfig)
}

val javaVersion = libs.versions.java.get()

buildConfig {
    className("VersionCatalog")
    packageName("io.github.denismarkushin.gradle.kotlinlibrary")
    buildConfigField("JAVA_VERSION", javaVersion.toInt())
}

dependencies {
    implementation(plugin(libs.plugins.kotlin.jvm))
    implementation(plugin(libs.plugins.kotlin.spring))
    implementation(plugin(libs.plugins.vanniktechMavenPublish))
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("kotlin-library") {
            id = "io.github.denis-markushin.kotlin-library"
            displayName = "Kotlin Library Convention Plugin"
            description = """
                Opinionated convention plugin for Kotlin library projects.
                Pre-configures Kotlin JVM and kotlin-spring compiler plugin, with a standard
                testing stack (JUnit 5, assertk) — so library authors can focus on API design.
            """.trimIndent()
            implementationClass = "io.github.denismarkushin.gradle.LibraryPlugin"
            tags.set(
                listOf(
                    "kotlin",
                    "library",
                    "convention-plugin",
                ),
            )
        }
    }
}
