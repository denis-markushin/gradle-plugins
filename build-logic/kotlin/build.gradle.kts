import org.gradle.plugin.compatibility.compatibility

plugins {
    alias(libs.plugins.buildconfig)
}

val javaVersion = libs.versions.java.get()

buildConfig {
    className("VersionCatalog")
    packageName("org.demalab.gradle.kotlinlibrary")
    buildConfigField("JAVA_VERSION", javaVersion.toInt())
}

dependencies {
    // api exposes kotlin-gradle-plugin types (KaptExtension, KotlinCompile, etc.) to consumers
    api(plugin(libs.plugins.kotlin.jvm))
    implementation(plugin(libs.plugins.kover))
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("kotlin-conventions") {
            id = "org.demalab.kotlin"
            displayName = "Kotlin Convention Plugin"
            description = """
                Opinionated convention plugin that applies and configures the Kotlin JVM toolchain,
                compiler arguments (-Xjsr305=strict, -Xemit-jvm-type-annotations, allWarningsAsErrors),
                javaParameters, and standard repositories (mavenLocal, mavenCentral).
                Used as a shared foundation by kotlin-library and spring-service convention plugins.
            """.trimIndent()
            implementationClass = "org.demalab.gradle.KotlinPlugin"
            tags.set(
                listOf(
                    "kotlin",
                    "kotlin-jvm",
                    "convention-plugin",
                    "compiler-options",
                ),
            )
            compatibility {
                features {
                    configurationCache = false
                }
            }
        }
    }
}
