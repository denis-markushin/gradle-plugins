dependencies {
    implementation(plugin(libs.plugins.spotless))
}

internal fun plugin(provider: Provider<PluginDependency>) = with(provider.get()) {
    "$pluginId:$pluginId.gradle.plugin:$version"
}

gradlePlugin {
    plugins {
        create("spotless-conventions") {
            id = "io.github.denis-markushin.spotless"
            displayName = "Spotless Code Formatting Convention Plugin"
            description = """
                Opinionated convention plugin for Spotless code formatting.
                Pre-configures Spotless with Kotlin and other formatting rules
                to enforce consistent code style across all modules.
            """.trimIndent()
            implementationClass = "io.github.denismarkushin.gradle.SpotlessPlugin"
            tags.set(
                listOf(
                    "spotless",
                    "kotlin",
                    "code-formatting",
                    "convention-plugin",
                    "code-style",
                ),
            )
        }
    }
}
