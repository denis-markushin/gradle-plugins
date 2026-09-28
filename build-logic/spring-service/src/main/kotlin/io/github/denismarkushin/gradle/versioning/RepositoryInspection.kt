package io.github.denismarkushin.gradle.versioning

import com.akuleshov7.vercraft.CHECKOUT_BRANCH
import com.akuleshov7.vercraft.DEFAULT_MAIN_BRANCH
import com.akuleshov7.vercraft.REMOTE
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

private val CANDIDATES = listOf("master", "main")

private val BRANCH_VARIABLES = listOf("CI_COMMIT_REF_NAME", "GITHUB_HEAD_REF", "BITBUCKET_BRANCH", "VERCRAFT_BRANCH")

private const val DEFAULT_BRANCH_VARIABLE = "CI_DEFAULT_BRANCH"

/**
 * Decides how a service gets its version, before Vercraft 0.8.0 is applied.
 *
 * Vercraft crashes in a folder without commits, on a detached HEAD when no
 * property or CI variable names the branch, and when the main branch is not
 * `main` while `defaultMainBranch` is unset. The inspection reads the
 * repository once and returns either a snapshot with the reason, or Vercraft
 * with the main branch: `CI_DEFAULT_BRANCH` when CI sets it, because CI
 * pipelines often fetch only their own ref before Vercraft's own fetch,
 * otherwise the first of `master` and `main` found locally or on the remote.
 */
internal class RepositoryInspection(
    private val dir: File,
    private val property: (String) -> Any?,
    private val env: (String) -> String?,
) {
    fun versioning(): Versioning {
        val builder = FileRepositoryBuilder().findGitDir(dir)
        if (builder.gitDir == null) return Versioning.Snapshot("folder $dir is not inside a git repository")
        return builder.build().use { verdict(it) }
    }

    private fun verdict(repository: Repository): Versioning =
        when {
            repository.resolve(Constants.HEAD) == null -> Versioning.Snapshot("repository ${repository.directory} has no commits")
            detached(repository) -> Versioning.Snapshot("HEAD of ${repository.directory} is detached and nothing names its branch")
            property(DEFAULT_MAIN_BRANCH) != null -> Versioning.Vercraft(null)
            else -> Versioning.Vercraft(branch(repository))
        }

    private fun detached(repository: Repository): Boolean =
        !repository.fullBranch.startsWith(Constants.R_HEADS) && property(CHECKOUT_BRANCH) == null && BRANCH_VARIABLES.all { env(it) == null }

    private fun branch(repository: Repository): String? {
        val remote = property(REMOTE)?.toString() ?: Constants.DEFAULT_REMOTE_NAME
        return env(DEFAULT_BRANCH_VARIABLE) ?: CANDIDATES.firstOrNull { present(repository, Constants.R_HEADS + it) || present(repository, "${Constants.R_REMOTES}$remote/$it") }
    }

    private fun present(repository: Repository, ref: String): Boolean = repository.exactRef(ref) != null
}
