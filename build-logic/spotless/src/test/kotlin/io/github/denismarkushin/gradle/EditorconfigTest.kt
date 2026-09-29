package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.concurrent.TimeUnit

class EditorconfigTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `spotlessCheck accepts multiline lambda passed as call argument`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        val source = """
            package sample

            fun sample() {
                executor.execute(mutation {
                    position { create(input = CreatePositionInput(id = id, fullName = name, shortName = "Сом")) { status } }
                })
            }
        """.trimIndent() + "\n"
        val result = SpotlessFixture.check(dir, source)
        assertThat(result.task(":spotlessCheck")?.outcome, "spotlessCheck rejected a lambda passed as call argument").isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `spotlessCheck skips code between spotless toggles`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        val source = """
            package sample

            // spotless:off
            fun sample(){ }
            // spotless:on
        """.trimIndent() + "\n"
        val result = SpotlessFixture.check(dir, source)
        assertThat(result.task(":spotlessCheck")?.outcome, "spotlessCheck ignored the spotless:off toggle").isEqualTo(TaskOutcome.SUCCESS)
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    fun `spotlessCheck rejects unused import`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        val source = """
            package sample

            import java.util.concurrent.ConcurrentSkipListMap

            fun sample() = Unit
        """.trimIndent() + "\n"
        val result = SpotlessFixture.check(dir, source)
        assertThat(result.task(":spotlessKotlinCheck")?.outcome, "spotlessCheck accepted an unused import").isEqualTo(TaskOutcome.FAILED)
    }
}
