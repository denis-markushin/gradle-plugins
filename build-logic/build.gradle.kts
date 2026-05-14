import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl` apply false
    alias(libs.plugins.plugin.publish) apply false
    alias(libs.plugins.vercraft)
}

val javaVersion = libs.versions.java.get()
val assertkDep = libs.assertk
val junitJupiterDep = libs.junit.jupiter
val junitLauncherDep = libs.junit.launcher

subprojects {
    plugins.apply("org.gradle.kotlin.kotlin-dsl")
    plugins.apply("com.gradle.plugin-publish")

    extensions.configure<GradlePluginDevelopmentExtension> {
        website.set(property("WEBSITE").toString())
        vcsUrl.set(property("VCS_URL").toString())
    }

    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }
            withSourcesJar()
            withJavadocJar()
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            javaParameters = true
            allWarningsAsErrors = true
        }
    }

    tasks.withType<AbstractPublishToMaven>().configureEach {
        doLast {
            publication.apply {
                logger.lifecycle("Published $groupId:$artifactId:$version artifact")
            }
        }
    }

    dependencies {
        "testImplementation"(gradleTestKit())
        "testImplementation"(assertkDep)
        "testImplementation"(junitJupiterDep)
        "testRuntimeOnly"(junitLauncherDep)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
