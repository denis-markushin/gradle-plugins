package io.github.denismarkushin.gradle

import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

/**
 * A throwaway Gradle build with the spotless convention plugin and one Kotlin file.
 *
 * The build has no `.editorconfig`, so the plugin bootstraps its own and the
 * check runs with exactly the rules services get. It is a git repository
 * because Spotless reads line endings from `.gitattributes`.
 */
internal object SpotlessFixture {
    fun check(dir: File, source: String): BuildResult {
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"sample\"\n")
        File(dir, "build.gradle.kts").writeText("plugins {\n    id(\"io.github.denis-markushin.spotless\")\n}\n\nrepositories {\n    mavenCentral()\n}\n")
        File(dir, "src/main/kotlin").mkdirs()
        File(dir, "src/main/kotlin/Sample.kt").writeText(source)
        Git.init().setDirectory(dir).call().close()
        return GradleRunner.create().withProjectDir(dir).withPluginClasspath().withArguments("spotlessCheck").run()
    }
}
