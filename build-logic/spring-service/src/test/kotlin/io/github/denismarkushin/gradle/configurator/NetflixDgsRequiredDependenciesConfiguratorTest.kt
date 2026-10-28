package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import io.github.denismarkushin.gradle.GitFixture
import io.github.denismarkushin.gradle.springservice.VersionCatalog.DEMA_GRAPHQL_STARTER_MODULE
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.concurrent.TimeUnit

class NetflixDgsRequiredDependenciesConfiguratorTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `codegen resolves starter version declared by service`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        dgsService(dir, "implementation(\"io.github.denis-markushin:graphql-dgs-starter:2.1.0\")")
        val result = runner(dir, "dependencyInsight", "--configuration", "dgsCodegen", "--dependency", "graphql-dgs-starter").build()
        assertThat(result.output, "codegen ignored the starter version the service declares").contains("$DEMA_GRAPHQL_STARTER_MODULE -> 2.1.0")
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `codegen fails when the service declares no starter`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        dgsService(dir, "")
        val result = runner(dir, "generateJava").buildAndFail()
        assertThat(result.output, "codegen invented a starter version the service never declared").contains(DEMA_GRAPHQL_STARTER_MODULE)
    }

    private fun dgsService(dir: File, dependency: String) {
        GitFixture.committed(dir, "master").close()
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"sample-service\"\n")
        File(dir, "build.gradle.kts").writeText(
            """
            plugins {
                id("io.github.denis-markushin.spring-service")
            }

            repositories {
                mavenCentral()
            }

            platform {
                useJooq.set(false)
                spring {
                    netflixDgs {
                        useNetflixDgs.set(true)
                    }
                }
            }

            dependencies {
                $dependency
            }
            """.trimIndent(),
        )
    }

    private fun runner(dir: File, vararg arguments: String): GradleRunner =
        GradleRunner.create()
            .withProjectDir(dir)
            .withPluginClasspath()
            .withArguments(*arguments)
}
