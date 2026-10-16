package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

class JooqCodegenTaskTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `adding a script to the included directory makes codegen out of date`(@TempDir dir: File) {
        val build = CodegenBuild(dir)
        build.run()
        build.script("${UUID.randomUUID()}.sql", "alter table spaceship alter column launched type timestamptz;")
        assertThat(build.run(), "codegen must rerun when a new included script appears, yet it stayed up to date")
            .isEqualTo(TaskOutcome.SUCCESS)
    }
}

/**
 * A throwaway build with the jOOQ codegen plugin and a Liquibase master changelog that
 * pulls its scripts in through `includeAll`. The codegen action is swapped for a no-op,
 * so the build checks only up-to-date tracking and needs neither Docker nor the network.
 */
private class CodegenBuild(
    private val dir: File,
) {

    private val liquibase = File(dir, "src/main/resources/liquibase")

    init {
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"codegen\"\n")
        File(dir, "build.gradle.kts").writeText(
            """
            plugins {
                java
                id("io.github.denis-markushin.jooq-codegen")
            }
            afterEvaluate {
                tasks.named<io.github.denismarkushin.gradle.JooqCodegenTask>("jooqCodegen") {
                    codegenClasspath.setFrom()
                    actions.clear()
                    doLast { }
                }
            }
            """.trimIndent(),
        )
        File(liquibase, "scripts").mkdirs()
        File(liquibase, "changelog-master.yml").writeText(
            """
            databaseChangeLog:
              - includeAll:
                  path: scripts
                  relativeToChangelogFile: true
            """.trimIndent(),
        )
        script("001-init.sql", "create table spaceship (launched timestamp);")
    }

    fun script(name: String, sql: String) {
        File(liquibase, "scripts/$name").writeText(sql)
    }

    fun run(): TaskOutcome? =
        GradleRunner.create()
            .withProjectDir(dir)
            .withPluginClasspath()
            .withArguments("jooqCodegen")
            .run()
            .task(":jooqCodegen")
            ?.outcome
}
