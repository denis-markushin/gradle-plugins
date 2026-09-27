package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import io.github.denismarkushin.gradle.GitFixture
import io.github.denismarkushin.gradle.springservice.VersionCatalog.DEMA_GRAPHQL_STARTER_DEP
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
                implementation("io.github.denis-markushin:graphql-dgs-starter:2.1.0")
            }
            """.trimIndent(),
        )
        val result = GradleRunner.create()
            .withProjectDir(dir)
            .withPluginClasspath()
            .withArguments("dependencyInsight", "--configuration", "dgsCodegen", "--dependency", "graphql-dgs-starter")
            .build()
        assertThat(result.output, "codegen ignored the starter version the service declares").contains("$DEMA_GRAPHQL_STARTER_DEP -> 2.1.0")
    }
}
