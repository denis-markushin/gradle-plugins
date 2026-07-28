package io.github.denismarkushin.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.util.jaxb.tools.MiniJAXB
import java.net.URLClassLoader

/**
 * Runs jOOQ code generation in a child classloader built from the `jooqCodegen` configuration,
 * so the codegen classpath (JDBC driver, database provider, codegen extensions) stays
 * independent of the plugin's own classpath.
 */
@CacheableTask
abstract class JooqCodegenTask : DefaultTask() {

    /** The merged jOOQ configuration, marshalled to XML. */
    @get:Input
    abstract val configurationXml: Property<String>

    /** Liquibase changelog; an input so codegen reruns on content change. Tracks the master only, not `include`d children. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val changelogFile: RegularFileProperty

    /** Classpath used to run the generator. */
    @get:Classpath
    abstract val codegenClasspath: ConfigurableFileCollection

    /** Directory the generated sources are written to. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val urls = codegenClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        URLClassLoader(urls, javaClass.classLoader).use { child ->
            val previous = Thread.currentThread().contextClassLoader
            try {
                Thread.currentThread().contextClassLoader = child
                @Suppress("UnstableApiUsage")
                GenerationTool.generate(MiniJAXB.unmarshal(configurationXml.get(), Configuration::class.java))
            } finally {
                Thread.currentThread().contextClassLoader = previous
            }
        }
    }
}
