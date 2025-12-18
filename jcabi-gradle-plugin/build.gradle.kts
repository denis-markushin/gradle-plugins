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
    implementation(project(":common-lib"))
    implementation(rootProject.libs.postCompileWeaving.plugin)
}
