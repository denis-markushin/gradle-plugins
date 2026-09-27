package io.github.denismarkushin.gradle.versioning

/**
 * Outcome of inspecting the service repository before Vercraft is applied.
 *
 * [Vercraft] means Vercraft can compute the version; its branch is the main
 * branch to hand over, or null when Vercraft keeps its own setting.
 * [Snapshot] means Vercraft would crash; its reason says why.
 */
internal sealed interface Versioning {
    data class Vercraft(
        val branch: String?,
    ) : Versioning

    data class Snapshot(
        val reason: String,
    ) : Versioning
}
