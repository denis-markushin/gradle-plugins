plugins {
    alias(libs.plugins.buildconfig)
}

buildConfig {
    className("VersionCatalog")
    packageName(project.group.toString().replace("-", ""))
    useKotlinOutput()
    useKotlinOutput { internalVisibility = false }

    buildConfigField("JCABI_ASPECTS_DEP", libs.jcabiAspects.get().toString())
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

dependencies {
    implementation(rootProject.libs.postCompileWeaving.plugin)
}
