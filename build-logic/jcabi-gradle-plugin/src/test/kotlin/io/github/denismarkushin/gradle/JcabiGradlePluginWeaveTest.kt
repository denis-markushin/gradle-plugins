package io.github.denismarkushin.gradle

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class JcabiGradlePluginWeaveTest {

    @Test
    fun `incremental rebuild with build cache keeps every woven method callable`(@TempDir dir: File) {
        val project = WeaveProject(dir)
        project.write(secondMethod = false)
        project.run()
        project.write(secondMethod = true)

        val output = project.run().output

        assertThat(output, "rewoven class must call the added method and the untouched class")
            .contains("second true", "bye")
    }

    @Test
    fun `incremental rebuild with build cache leaves no stale closure classes`(@TempDir dir: File, @TempDir clean: File) {
        val project = WeaveProject(dir)
        project.write(secondMethod = true)
        project.run()
        project.write(secondMethod = false)
        project.run()
        val reference = WeaveProject(clean)
        reference.write(secondMethod = false)
        reference.run()

        assertThat(project.closures(), "incremental weave must produce the same closure classes as a clean weave")
            .isEqualTo(reference.closures())
    }
}

private class WeaveProject(
    private val dir: File,
) {

    private val cache = File(dir, "cache")

    fun write(secondMethod: Boolean) {
        File(dir, "settings.gradle.kts").writeText(
            """
            buildCache { local { directory = file("${cache.invariantSeparatorsPath}") } }
            rootProject.name = "weave"
            """.trimIndent(),
        )
        File(dir, "build.gradle.kts").writeText(
            """
            plugins {
                kotlin("jvm")
                application
                id("io.github.denis-markushin.jcabi-gradle-plugin")
            }
            repositories { mavenCentral() }
            dependencies { implementation("org.aspectj:aspectjrt:1.9.25.1") }
            application { mainClass.set("example.MainKt") }
            """.trimIndent(),
        )
        val src = File(dir, "src/main/kotlin/example").apply { mkdirs() }
        val second = if (secondMethod) "@Loggable fun second(flag: Boolean): String = \"second ${'$'}flag\"" else ""
        File(src, "Greeter.kt").writeText(
            """
            package example
            import com.jcabi.aspects.Loggable
            class Greeter {
                @Loggable fun first(name: String): String = "first ${'$'}name"
                $second
                @Loggable fun third(name: String, times: Int): String = "third ${'$'}name ${'$'}times"
            }
            """.trimIndent(),
        )
        File(src, "Farewell.kt").writeText(
            """
            package example
            import com.jcabi.aspects.Loggable
            class Farewell {
                @Loggable fun bye(): String = "bye"
            }
            """.trimIndent(),
        )
        val call = if (secondMethod) "println(greeter.second(true))" else ""
        File(src, "Main.kt").writeText(
            """
            package example
            fun main() {
                val greeter = Greeter()
                println(greeter.first("x"))
                $call
                println(greeter.third("y", 7))
                println(Farewell().bye())
            }
            """.trimIndent(),
        )
    }

    fun run() = GradleRunner.create()
        .withProjectDir(dir)
        .withPluginClasspath()
        .withArguments("run", "--build-cache", "--stacktrace")
        .build()

    fun closures() = File(dir, "build/classes/kotlin/main/example")
        .listFiles { file -> file.name.contains("\$AjcClosure") }!!
        .map { it.name to it.readBytes().toList() }
        .sortedBy { it.first }
}
