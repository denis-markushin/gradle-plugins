package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.denismarkushin.gradle.GitFixture
import io.github.denismarkushin.gradle.ServiceFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import org.gradle.api.tasks.testing.Test as TestTask

class TestConfiguratorTest {

    @Test
    fun `service tests run under a default junit timeout`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val project = ServiceFixture.project(dir)
        assertThat(
            project.tasks.withType(TestTask::class.java).getByName("test").systemProperties["junit.jupiter.execution.timeout.default"],
            "service tests can hang forever without a default JUnit timeout",
        ).isEqualTo("5m")
    }

    @Test
    fun `service tests drop the junit timeout under a debugger`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val project = ServiceFixture.project(dir)
        assertThat(
            project.tasks.withType(TestTask::class.java).getByName("test").systemProperties["junit.jupiter.execution.timeout.mode"],
            "JUnit timeout kills a test paused at a debugger breakpoint",
        ).isEqualTo("disabled_on_debug")
    }
}
