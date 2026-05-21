package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.jooq.VersionCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jooq.codegen.gradle.MetaExtensions.ConfigurationExtension
import org.jooq.meta.jaxb.Configuration
import org.jooq.util.jaxb.tools.MiniJAXB
import java.io.File

@Suppress("UnstableApiUsage")
abstract class JooqCodegenPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        configurations.create(CODEGEN_NAME) {
            description = "Classpath for jOOQ code generation: JDBC driver, database provider, codegen extensions."
        }
        val ext = extensions.create(JooqCodegenExtension.NAME, JooqCodegenExtension::class.java)

        // JooqConfigurator sets enabled/databaseImage/jooqVersion/configAction via lazy providers,
        // so the platform { } block must finish evaluating before we read them — hence afterEvaluate.
        afterEvaluate {
            if (!ext.enabled.get()) return@afterEvaluate
            injectDependencies(ext)
            val outputDir = layout.buildDirectory.dir(GENERATED_DIR).get().asFile
            val configurationXml = MiniJAXB.marshal(buildConfiguration(ext, outputDir.absolutePath))
            registerCodegenTask(configurationXml, outputDir)
            wireSourceSet(outputDir)
        }
    }

    private fun Project.injectDependencies(ext: JooqCodegenExtension) {
        dependencies.add("implementation", "org.jooq:jooq:${ext.jooqVersion.get()}")
        dependencies.add(CODEGEN_NAME, VersionCatalog.JOOQ_LIQUIBASE_TC_DEP)
    }

    /**
     * Builds the jOOQ configuration by layering an optional consumer override on top of the dema
     * defaults. The two are kept as separate trees and deep-merged with `MiniJAXB.append`, exactly
     * as the official jOOQ plugin merges successive `configuration { }` calls. A single shared
     * object would not work: jOOQ's `generator { }` DSL replaces rather than mutates.
     */
    private fun Project.buildConfiguration(ext: JooqCodegenExtension, targetDir: String): Configuration {
        val changelogFile = "$projectDir/src/main/resources/liquibase/changelog-master.yml"
        val packageName = group.toString().replace("-", "")

        val defaults = objects.newInstance(ConfigurationExtension::class.java, objects)
        defaults.applyDemaDefaults(
            databaseImage = ext.databaseImage.get(),
            changelogFile = changelogFile,
            targetDir = targetDir,
            packageName = packageName,
        )

        val consumer = ext.configAction.orNull ?: return copyOf(defaults)
        val override = objects.newInstance(ConfigurationExtension::class.java, objects)
        consumer.execute(override)
        return MiniJAXB.append(copyOf(override), copyOf(defaults))
    }

    private fun copyOf(configuration: Configuration): Configuration =
        MiniJAXB.unmarshal(MiniJAXB.marshal(configuration), Configuration::class.java)

    private fun Project.registerCodegenTask(configurationXml: String, outputDir: File) {
        tasks.register<JooqCodegenTask>(CODEGEN_NAME) {
            group = "jooq"
            description = "Generates jOOQ sources from the Liquibase changelog."
            this.configurationXml.set(configurationXml)
            codegenClasspath.from(configurations.getByName(CODEGEN_NAME))
            outputDirectory.fileValue(outputDir)
        }
    }

    private fun Project.wireSourceSet(outputDir: File) {
        val sourceSets = extensions.getByType(SourceSetContainer::class.java)
        val jooq = sourceSets.create("jooq") { java.srcDir(outputDir) }

        configurations.getByName("jooqImplementation")
            .extendsFrom(configurations.getByName("implementation"))

        sourceSets["main"].compileClasspath += jooq.output
        sourceSets["main"].runtimeClasspath += jooq.output
        sourceSets["test"].compileClasspath += jooq.output
        sourceSets["test"].runtimeClasspath += jooq.output

        tasks.withType<Jar>().configureEach { from(jooq.output) }

        tasks.named("compileJooqJava").configure { dependsOn(CODEGEN_NAME) }
        tasks.matching { it.name == "compileKotlin" }.configureEach { dependsOn(CODEGEN_NAME) }
        tasks.matching { it.name.startsWith("kaptGenerateStubs") }.configureEach { dependsOn(CODEGEN_NAME) }
    }

    private companion object {
        /** Name shared by the codegen Gradle configuration and the codegen task (distinct namespaces). */
        const val CODEGEN_NAME = "jooqCodegen"
        const val GENERATED_DIR = "generated/sources/jooq"
    }
}
