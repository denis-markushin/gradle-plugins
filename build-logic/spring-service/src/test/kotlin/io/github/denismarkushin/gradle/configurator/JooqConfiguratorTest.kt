package io.github.denismarkushin.gradle.configurator

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import io.github.denismarkushin.gradle.GitFixture
import io.github.denismarkushin.gradle.ServiceFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File

class JooqConfiguratorTest {

    @Test
    fun `service with jooq declares liquibase starter`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val project = ServiceFixture.project(dir) { useJooq.set(true) }
        val names = project.configurations.getByName("implementation").dependencies.map { "${it.group}:${it.name}" }
        assertThat(names, "service with jOOQ has no Liquibase starter and skips its migrations").contains("org.springframework.boot:spring-boot-starter-liquibase")
    }

    @Test
    fun `service without jooq omits liquibase starter`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val project = ServiceFixture.project(dir) { useJooq.set(false) }
        val names = project.configurations.getByName("implementation").dependencies.map { "${it.group}:${it.name}" }
        assertThat(names, "service without jOOQ got a Liquibase starter it never asked for").doesNotContain("org.springframework.boot:spring-boot-starter-liquibase")
    }
}
