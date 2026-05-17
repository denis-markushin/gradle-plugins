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
    // api exposes kotlin-gradle-plugin types (KaptExtension, KotlinCompile, etc.) to consumers
    api(plugin(libs.plugins.kotlin.jvm))
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("kotlin-conventions") {
            id = "io.github.denis-markushin.kotlin"
            displayName = "Kotlin Convention Plugin"
            description = """
                Opinionated convention plugin that applies and configures the Kotlin JVM toolchain,
                compiler arguments (-Xjsr305=strict, -Xemit-jvm-type-annotations, allWarningsAsErrors),
                javaParameters, and standard repositories (mavenLocal, mavenCentral).
                Used as a shared foundation by kotlin-library and spring-service convention plugins.
            """.trimIndent()
            implementationClass = "io.github.denismarkushin.gradle.KotlinPlugin"
            tags.set(
                listOf(
                    "kotlin",
                    "kotlin-jvm",
                    "convention-plugin",
                    "compiler-options",
                ),
            )
        }
    }
}
