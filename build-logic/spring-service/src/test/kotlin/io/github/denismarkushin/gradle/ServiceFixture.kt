package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * A service project with the plugin applied and evaluated.
 *
 * The `platform` extension is configured before evaluation, the same order a
 * real `build.gradle.kts` produces, so `afterEvaluate` hooks see the values.
 * Formatting files are stubbed so the spotless plugin does not bootstrap them.
 */
internal object ServiceFixture {
    fun project(dir: File, configure: DemaPlatformExtension.() -> Unit = {}): Project {
        File(dir, ".editorconfig").writeText("[*]\nend_of_line = lf\n")
        File(dir, ".gitattributes").writeText("* text=auto\n")
        val project = ProjectBuilder.builder().withProjectDir(dir).build()
        project.plugins.apply(SpringBootServicePlugin::class.java)
        project.extensions.getByType(DemaPlatformExtension::class.java).configure()
        (project as ProjectInternal).evaluate()
        return project
    }
}
