import org.gradle.plugin.compatibility.compatibility

dependencies {
    implementation(plugin(libs.plugins.spotless))
    testImplementation(libs.jgit)
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("spotless-conventions") {
            id = "org.demalab.spotless"
            displayName = "Spotless Code Formatting Convention Plugin"
            description = """
                Opinionated convention plugin for Spotless code formatting.
                Pre-configures Spotless with Kotlin and other formatting rules
                to enforce consistent code style across all modules.
            """.trimIndent()
            implementationClass = "org.demalab.gradle.SpotlessPlugin"
            tags.set(
                listOf(
                    "spotless",
                    "kotlin",
                    "code-formatting",
                    "convention-plugin",
                    "code-style",
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
