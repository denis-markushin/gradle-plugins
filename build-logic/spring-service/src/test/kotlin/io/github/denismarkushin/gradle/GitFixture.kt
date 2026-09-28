package io.github.denismarkushin.gradle

import org.eclipse.jgit.api.Git
import java.io.File

/**
 * Real git repositories for plugin tests.
 *
 * Vercraft and the repository inspection read an actual `.git` folder, so
 * tests create one in a temporary folder instead of faking git.
 */
internal object GitFixture {
    fun empty(dir: File, branch: String): Git = Git.init().setDirectory(dir).setInitialBranch(branch).call()

    fun committed(dir: File, branch: String): Git =
        empty(dir, branch).also {
            it.commit()
                .setMessage("init")
                .setAllowEmpty(true)
                .setSign(false)
                .setAuthor("Test", "test@example.com")
                .setCommitter("Test", "test@example.com")
                .call()
        }
}
