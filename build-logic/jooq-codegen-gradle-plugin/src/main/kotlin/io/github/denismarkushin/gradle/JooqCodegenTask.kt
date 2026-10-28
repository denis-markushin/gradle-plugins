package io.github.denismarkushin.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Runs jOOQ code generation in a separate JVM whose classpath is exactly the `jooqCodegen`
 * configuration: the generator of the service's jOOQ version, its driver, Liquibase and the
 * Testcontainers provider. No plugin class is on that classpath, so the plugin's own jOOQ (used
 * only to build the configuration XML) can never shadow the service's.
 */
@CacheableTask
abstract class JooqCodegenTask : DefaultTask() {

    /** The merged jOOQ configuration, marshalled to XML. */
    @get:Input
    abstract val configurationXml: Property<String>

    /** Liquibase master changelog; an input so codegen reruns on content change. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val changelogFile: RegularFileProperty

    /** Directory of the master changelog; an input so scripts pulled in by `include`/`includeAll` rerun codegen too. */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val changelogDirectory: DirectoryProperty

    /** Classpath used to run the generator. */
    @get:Classpath
    abstract val codegenClasspath: ConfigurableFileCollection

    /** Directory the generated sources are written to. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val xml = File(temporaryDir, "codegen.xml")
        xml.writeText(configurationXml.get())
        execOperations.javaexec {
            classpath(codegenClasspath)
            mainClass.set("org.jooq.codegen.GenerationTool")
            args(xml.absolutePath)
        }
    }
}
