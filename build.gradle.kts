import com.diffplug.spotless.LineEnding
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.plugin.publish) apply false
    alias(libs.plugins.spotless)
}

subprojects {
    if (name.endsWith("plugin")) {
        plugins.apply(rootProject.libs.plugins.plugin.publish.get().pluginId)
        extensions.configure<GradlePluginDevelopmentExtension> {
            website.set(property("WEBSITE").toString())
            vcsUrl.set(property("VCS_URL").toString())
        }
    }

    plugins.apply(rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withSourcesJar()
            withJavadocJar()
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    tasks.withType<AbstractPublishToMaven> {
        doLast {
            publication.apply {
                logger.lifecycle("Published $groupId:$artifactId:$version artifact")
            }
        }
    }
}

spotless {
    lineEndings = LineEnding.GIT_ATTRIBUTES_FAST_ALLSAME
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint().setEditorConfigPath(project.file(".editorconfig").path)
        toggleOffOn()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint().setEditorConfigPath(project.file(".editorconfig").path)
        toggleOffOn()
    }
}

tasks.assemble {
    dependsOn("spotlessInstallGitPrePushHook")
}

tasks.named("spotlessInstallGitPrePushHook") {
    onlyIf { !file(".git/hooks/pre-push").exists() }
}

private fun Project.value(name: String, default: String? = null) =
    requireNotNull(findProperty(name)?.toString() ?: System.getenv(name) ?: default) {
        "Please setup '$name' variable"
    }
