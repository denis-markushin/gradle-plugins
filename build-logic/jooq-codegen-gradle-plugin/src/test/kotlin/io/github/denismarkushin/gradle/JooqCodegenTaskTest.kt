package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.exists
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Runs the real `jooqCodegen` task against a Docker PostgreSQL: the only test that sees the
 * whole codegen classpath the plugin assembles, generator included.
 */
class JooqCodegenTaskTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `jooqCodegen generates a table class from the changelog`(@TempDir dir: File) {
        sampleProject(dir, "")
        GradleRunner.create()
            .withProjectDir(dir)
            .withPluginClasspath()
            .withArguments("jooqCodegen")
            .build()
        assertThat(File(dir, "build/generated/sources/jooq/com/acme/tables/Book.java"), "jooqCodegen produced no Book table class").exists()
    }

    private fun sampleProject(dir: File, demaJooqBlock: String) {
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"sample-service\"\n")
        File(dir, "build.gradle.kts").writeText(
            """
            plugins {
                java
                id("io.github.denis-markushin.jooq-codegen")
            }

            group = "com.acme"

            repositories {
                mavenCentral()
            }

            $demaJooqBlock
            """.trimIndent(),
        )
        val changelog = File(dir, "src/main/resources/liquibase/changelog-master.yml")
        changelog.parentFile.mkdirs()
        changelog.writeText(
            """
            databaseChangeLog:
              - changeSet:
                  id: create-book
                  author: test
                  changes:
                    - createTable:
                        tableName: book
                        columns:
                          - column:
                              name: id
                              type: uuid
                              constraints:
                                primaryKey: true
                          - column:
                              name: title
                              type: text
                          - column:
                              name: created_at
                              type: timestamptz
            """.trimIndent(),
        )
    }
}
