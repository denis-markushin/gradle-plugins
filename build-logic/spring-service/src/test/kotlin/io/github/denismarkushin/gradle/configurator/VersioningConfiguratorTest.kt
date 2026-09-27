package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import io.github.denismarkushin.gradle.GitFixture
import io.github.denismarkushin.gradle.ServiceFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File

class VersioningConfiguratorTest {

    @Test
    fun `service without commits gets snapshot version`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.empty(dir, "master").close()
        val project = ServiceFixture.project(dir)
        assertThat(project.version.toString(), "service without commits did not fall back to snapshot").isEqualTo("0.0.0-SNAPSHOT")
    }

    @Test
    fun `service on master branch gets vercraft version`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val project = ServiceFixture.project(dir)
        assertThat(project.version.toString(), "Vercraft did not compute the version from master").contains("master")
    }
}
