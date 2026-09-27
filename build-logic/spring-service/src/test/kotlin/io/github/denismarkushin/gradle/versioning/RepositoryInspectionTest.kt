package io.github.denismarkushin.gradle.versioning

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.denismarkushin.gradle.GitFixture
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.UUID

class RepositoryInspectionTest {

    @Test
    fun `versioning is snapshot outside git repository`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "folder outside git got a Vercraft version").isInstanceOf(Versioning.Snapshot::class)
    }

    @Test
    fun `versioning is snapshot in repository without commits`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.empty(dir, "topic-${UUID.randomUUID()}").close()
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "repository without commits got a Vercraft version").isInstanceOf(Versioning.Snapshot::class)
    }

    @Test
    fun `versioning is snapshot on detached head outside ci`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").use { it.checkout().setName(it.repository.resolve("HEAD").name).call() }
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "detached HEAD outside CI got a Vercraft version").isInstanceOf(Versioning.Snapshot::class)
    }

    @Test
    fun `versioning keeps vercraft on detached head in ci`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").use { it.checkout().setName(it.repository.resolve("HEAD").name).call() }
        val versioning = RepositoryInspection(dir, { null }, { if (it == "CI_COMMIT_REF_NAME") "master" else null }).versioning()
        assertThat(versioning, "detached HEAD in CI lost its Vercraft version").isEqualTo(Versioning.Vercraft("master"))
    }

    @Test
    fun `versioning leaves main branch to explicit property`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val versioning = RepositoryInspection(dir, { if (it == "defaultMainBranch") "trunk" else null }, { null }).versioning()
        assertThat(versioning, "explicit defaultMainBranch was overridden").isEqualTo(Versioning.Vercraft(null))
    }

    @Test
    fun `versioning picks local master branch`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "master").close()
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "local master was not handed to Vercraft").isEqualTo(Versioning.Vercraft("master"))
    }

    @Test
    fun `versioning picks local main branch`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "main").close()
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "local main was not handed to Vercraft").isEqualTo(Versioning.Vercraft("main"))
    }

    @Test
    fun `versioning picks remote master branch`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "topic-${UUID.randomUUID()}").use { git ->
            val update = git.repository.updateRef("refs/remotes/origin/master")
            update.setNewObjectId(git.repository.resolve("HEAD"))
            update.update()
        }
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "remote master was not handed to Vercraft").isEqualTo(Versioning.Vercraft("master"))
    }

    @Test
    fun `versioning leaves main branch unset without master and main`(@TempDir(cleanup = CleanupMode.NEVER) dir: File) {
        GitFixture.committed(dir, "topic-${UUID.randomUUID()}").close()
        val versioning = RepositoryInspection(dir, { null }, { null }).versioning()
        assertThat(versioning, "a made-up main branch was handed to Vercraft").isEqualTo(Versioning.Vercraft(null))
    }
}
