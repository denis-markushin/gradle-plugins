package io.github.denismarkushin.gradle

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SpringBootServicePluginTest {

    @Nested
    inner class UnitTests {

        @TempDir
        lateinit var projectDir: File

        private lateinit var project: Project

        @BeforeEach
        fun setUp() {
            // VercraftPlugin requires a real git repository
            File(projectDir, ".editorconfig").writeText("[*]\nend_of_line = lf\n")
            File(projectDir, ".gitattributes").writeText("* text=auto\n")
            File(projectDir, ".dockerignore").writeText("build/\n")
            File(projectDir, ".gitignore").writeText("build/\n")
            File(projectDir, "Dockerfile.build-image").writeText("FROM eclipse-temurin:21\n")
            File(projectDir, ".gitlab-ci.yml").writeText("stages: [build]\n")

            git("init", "-b", "main")
            git("config", "user.email", "test@test.com")
            git("config", "user.name", "Test")
            git("add", ".")
            git("-c", "commit.gpgsign=false", "commit", "-m", "init")

            project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build()
            project.plugins.apply(SpringBootServicePlugin::class.java)
            (project as org.gradle.api.internal.project.ProjectInternal).evaluate()
        }

        private fun git(vararg args: String) {
            val process = ProcessBuilder("git", *args)
                .directory(projectDir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            check(exitCode == 0) { "git ${args.toList()} failed (exit=$exitCode): $output" }
        }

        /**
         * Builds a project with [SpringBootServicePlugin] applied, configures the extension
         * via [configure] block BEFORE evaluation, then evaluates the project.
         */
        private fun buildProjectWithExtension(configure: DemaPlatformExtension.() -> Unit): Project {
            val p = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build()
            p.plugins.apply(SpringBootServicePlugin::class.java)
            p.extensions.getByType(DemaPlatformExtension::class.java).configure()
            (p as org.gradle.api.internal.project.ProjectInternal).evaluate()
            return p
        }

        @Test
        fun `plugin applies required plugins`() {
            assertAll {
                assertThat(project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")).isTrue()
                assertThat(project.plugins.hasPlugin("org.jetbrains.kotlin.plugin.spring")).isTrue()
                assertThat(project.plugins.hasPlugin("org.springframework.boot")).isTrue()
                assertThat(project.plugins.hasPlugin("org.jetbrains.kotlin.kapt")).isTrue()
                assertThat(project.plugins.hasPlugin("com.diffplug.spotless")).isTrue()
                assertThat(project.plugins.hasPlugin("com.gorylenko.gradle-git-properties")).isTrue()
            }
        }

        @Test
        fun `plugin registers platform extension with correct defaults`() {
            val ext = project.extensions.findByName(DemaPlatformExtension.NAME)
            assertThat(ext).isNotNull()

            val typed = project.extensions.getByType(DemaPlatformExtension::class.java)
            assertAll {
                assertThat(typed.useKapt.get()).isTrue()
                assertThat(typed.useJcabi.get()).isTrue()
                assertThat(typed.useMapstruct.get()).isFalse()
                assertThat(typed.spring.useCloud.get()).isFalse()
                assertThat(typed.spring.netflixDgs.useNetflixDgs.get()).isFalse()
            }
        }

        @Test
        fun `plugin adds Spring Boot BOM to implementation`() {
            val implDeps = project.configurations.getByName("implementation").dependencies
            val bomDep = implDeps.find {
                it.group == "org.springframework.boot" && it.name == "spring-boot-dependencies"
            }
            assertThat(bomDep).isNotNull()
        }

        @Test
        fun `plugin adds test dependencies`() {
            val testDeps = project.configurations.getByName("testImplementation").dependencies
            assertAll {
                assertThat(testDeps.any { it.group == "com.willowtreeapps.assertk" && it.name == "assertk-jvm" }).isTrue()
                assertThat(testDeps.any { it.group == "com.ninja-squad" && it.name == "springmockk" }).isTrue()
                assertThat(testDeps.any { it.group == "io.kotest" && it.name == "kotest-assertions-core" }).isTrue()
            }
        }

        @Test
        fun `plugin adds junit launcher to testRuntimeOnly`() {
            val runtimeDeps = project.configurations.getByName("testRuntimeOnly").dependencies
            assertThat(runtimeDeps.any { it.name == "junit-platform-launcher" }).isTrue()
        }

        @Test
        fun `plugin excludes mockito`() {
            val testImpl = project.configurations.getByName("testImplementation")
            assertThat(testImpl.excludeRules.any { it.group == "org.mockito" }).isTrue()
        }

        @Test
        fun `plugin adds kapt spring-boot-configuration-processor`() {
            val kaptDeps = project.configurations.getByName("kapt").dependencies
            val configProcessor = kaptDeps.find {
                it.group == "org.springframework.boot" && it.name == "spring-boot-configuration-processor"
            }
            assertThat(configProcessor).isNotNull()
        }

        @Test
        fun `plugin adds devtools to developmentOnly`() {
            val devOnlyDeps = project.configurations.getByName("developmentOnly").dependencies
            assertAll {
                assertThat(devOnlyDeps.any { it.name == "spring-boot-devtools" }).isTrue()
                assertThat(devOnlyDeps.any { it.name == "spring-boot-docker-compose" }).isTrue()
            }
        }

        @Test
        fun `plugin disables jar and sourcesJar tasks`() {
            assertAll {
                val jar = project.tasks.findByName("jar")
                if (jar != null) assertThat(jar.enabled).isFalse()
                val sourcesJar = project.tasks.findByName("sourcesJar")
                if (sourcesJar != null) assertThat(sourcesJar.enabled).isFalse()
            }
        }

        @Test
        fun `spring cloud deps not added when useCloud is false`() {
            val implDeps = project.configurations.getByName("implementation").dependencies
            assertThat(implDeps.any { it.name == "spring-cloud-dependencies" }).isFalse()
        }

        @Test
        fun `netflix dgs BOM not added when useNetflixDgs is false`() {
            val implDeps = project.configurations.getByName("implementation").dependencies
            assertThat(implDeps.any { it.name == "graphql-dgs-platform-dependencies" }).isFalse()
        }

        @Test
        fun `mapstruct deps not added when useMapstruct is false`() {
            val kaptDeps = project.configurations.getByName("kapt").dependencies
            assertThat(kaptDeps.any { it.name == "mapstruct-processor" }).isFalse()
        }

        @Test
        fun `mapstruct kapt deps added when useMapstruct is true`() {
            val p = buildProjectWithExtension { useMapstruct.set(true) }

            val kaptDeps = p.configurations.getByName("kapt").dependencies
            assertAll {
                assertThat(kaptDeps.any { it.name == "mapstruct-processor" }).isTrue()
                assertThat(kaptDeps.any { it.name == "mapstruct-spring-extensions" }).isTrue()
            }
        }

        @Test
        fun `mapstruct implementation deps added when useMapstruct is true`() {
            val p = buildProjectWithExtension { useMapstruct.set(true) }

            val implDeps = p.configurations.getByName("implementation").dependencies
            assertAll {
                assertThat(implDeps.any { it.name == "mapstruct" }).isTrue()
                assertThat(implDeps.any { it.name == "mapstruct-spring-annotations" }).isTrue()
            }
        }

        @Test
        fun `jcabi plugin applied when useJcabi is true`() {
            assertThat(project.plugins.hasPlugin(JcabiGradlePlugin::class.java)).isTrue()
        }

        @Test
        fun `plugin applies the jooq codegen plugin`() {
            val p = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build()
            p.plugins.apply(SpringBootServicePlugin::class.java)
            (p as org.gradle.api.internal.project.ProjectInternal).evaluate()
            assertThat(p.plugins.hasPlugin(JooqCodegenPlugin::class.java))
                .isTrue()
        }
    }

    @Nested
    inner class IntegrationTests {

        @TempDir
        lateinit var projectDir: File

        @BeforeEach
        fun setUp() {
            File(projectDir, "settings.gradle.kts").writeText(
                """rootProject.name = "test-service"""",
            )

            File(projectDir, ".editorconfig").writeText("[*]\nend_of_line = lf\n")
            File(projectDir, ".gitattributes").writeText("* text=auto\n")
            File(projectDir, ".dockerignore").writeText("build/\n")
            File(projectDir, ".gitignore").writeText("build/\n")
            File(projectDir, "Dockerfile.build-image").writeText("FROM eclipse-temurin:21\n")
            File(projectDir, ".gitlab-ci.yml").writeText("stages: [build]\n")

            git("init", "-b", "main")
            git("config", "user.email", "test@test.com")
            git("config", "user.name", "Test")
            git("add", ".")
            git("-c", "commit.gpgsign=false", "commit", "-m", "init")
        }

        private fun git(vararg args: String) {
            val process = ProcessBuilder("git", *args)
                .directory(projectDir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            check(exitCode == 0) { "git ${args.toList()} failed (exit=$exitCode): $output" }
        }

        private fun writeBuildFile(extraConfig: String = "") {
            File(projectDir, "build.gradle.kts").writeText(
                """
                plugins {
                    id("io.github.denis-markushin.spring-service")
                }

                repositories {
                    mavenCentral()
                }

                $extraConfig
                """.trimIndent(),
            )
        }

        private fun runner(vararg args: String) =
            GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(*args, "--stacktrace")

        @Test
        fun `plugin applies successfully with default configuration`() {
            writeBuildFile()

            val result = runner("tasks").build()

            assertThat(result.output).contains("BUILD SUCCESSFUL")
        }

        @Test
        fun `spring cloud dependencies are added when useCloud is true`() {
            writeBuildFile(
                """
                platform {
                    spring {
                        useCloud.set(true)
                    }
                }

                tasks.register("printImplDeps") {
                    doLast {
                        val impl = configurations.getByName("implementation")
                        impl.dependencies.forEach {
                            println("IMPL_DEP: ${'$'}{it.group}:${'$'}{it.name}")
                        }
                    }
                }
                """.trimIndent(),
            )

            val result = runner("printImplDeps").build()

            assertThat(result.output).contains("IMPL_DEP: org.springframework.cloud:spring-cloud-dependencies")
        }

        @Test
        fun `netflix dgs BOM is added when useNetflixDgs is true`() {
            writeBuildFile(
                """
                platform {
                    spring {
                        netflixDgs {
                            useNetflixDgs.set(true)
                        }
                    }
                }

                tasks.register("printImplDeps") {
                    doLast {
                        val impl = configurations.getByName("implementation")
                        impl.dependencies.forEach {
                            println("IMPL_DEP: ${'$'}{it.group}:${'$'}{it.name}")
                        }
                    }
                }
                """.trimIndent(),
            )

            val result = runner("printImplDeps").build()

            assertThat(result.output).contains("IMPL_DEP: com.netflix.graphql.dgs:graphql-dgs-platform-dependencies")
        }

        @Test
        fun `mapstruct dependencies are added when useMapstruct is true`() {
            writeBuildFile(
                """
                platform {
                    useMapstruct.set(true)
                }

                tasks.register("printDeps") {
                    doLast {
                        val kapt = configurations.getByName("kapt")
                        kapt.dependencies.forEach {
                            println("KAPT_DEP: ${'$'}{it.group}:${'$'}{it.name}")
                        }
                        val impl = configurations.getByName("implementation")
                        impl.dependencies.forEach {
                            println("IMPL_DEP: ${'$'}{it.group}:${'$'}{it.name}")
                        }
                    }
                }
                """.trimIndent(),
            )

            val result = runner("printDeps").build()

            assertAll {
                assertThat(result.output).contains("KAPT_DEP: org.mapstruct:mapstruct-processor")
                assertThat(result.output).contains("KAPT_DEP: org.mapstruct.extensions.spring:mapstruct-spring-extensions")
                assertThat(result.output).contains("IMPL_DEP: org.mapstruct:mapstruct")
                assertThat(result.output).contains("IMPL_DEP: org.mapstruct.extensions.spring:mapstruct-spring-annotations")
            }
        }

        @Test
        fun `processResources substitutes project tokens`() {
            writeBuildFile()

            File(projectDir, "src/main/resources").mkdirs()
            File(projectDir, "src/main/resources/application.properties").writeText(
                "spring.application.name=@project.name@",
            )

            File(projectDir, "src/main/kotlin").mkdirs()
            File(projectDir, "src/main/kotlin/App.kt").writeText(
                """
                import org.springframework.boot.autoconfigure.SpringBootApplication
                @SpringBootApplication
                class App
                """.trimIndent(),
            )

            val result = runner("processResources").build()

            assertThat(result.task(":processResources")!!.outcome).isEqualTo(TaskOutcome.SUCCESS)

            val processed = File(projectDir, "build/resources/main/application.properties").readText()
            assertAll {
                assertThat(processed).contains("test-service")
                assertThat(processed).doesNotContain("@project.name@")
            }
        }
    }
}
