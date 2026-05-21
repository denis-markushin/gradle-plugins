package io.github.denismarkushin.gradle

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.gradle.api.Action
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.testfixtures.ProjectBuilder
import org.jooq.codegen.gradle.MetaExtensions.ConfigurationExtension
import org.jooq.codegen.gradle.MetaExtensions.DatabaseExtension
import org.jooq.codegen.gradle.MetaExtensions.GeneratorExtension
import org.jooq.meta.jaxb.Configuration
import org.jooq.util.jaxb.tools.MiniJAXB
import org.junit.jupiter.api.Test

class JooqCodegenPluginTest {

    private fun project(group: String = "ru.easyway.demo"): ProjectInternal {
        val project = ProjectBuilder.builder().build() as ProjectInternal
        project.group = group
        project.plugins.apply(JavaPlugin::class.java)
        project.plugins.apply(JooqCodegenPlugin::class.java)
        return project
    }

    /** Reads back the merged configuration the plugin marshalled into the registered task. */
    private fun resolvedConfiguration(project: ProjectInternal): Configuration {
        val task = project.tasks.named("jooqCodegen", JooqCodegenTask::class.java).get()
        return MiniJAXB.unmarshal(task.configurationXml.get(), Configuration::class.java)
    }

    @Test
    fun `plugin registers the demaJooq extension`() {
        val project = project()
        assertThat(project.extensions.findByName(JooqCodegenExtension.NAME)).isNotNull()
    }

    @Test
    fun `plugin injects the jooq runtime dependency`() {
        val project = project()
        project.evaluate()

        val jooqDep = project.configurations.getByName("implementation").dependencies
            .find { it.group == "org.jooq" && it.name == "jooq" }
        assertThat(jooqDep).isNotNull()
    }

    @Test
    fun `plugin injects the jooqCodegen classpath dependency`() {
        val project = project()
        project.evaluate()

        val tcDep = project.configurations.getByName("jooqCodegen").dependencies
            .find { it.name == "jooq-liquibase-testcontainer" }
        assertThat(tcDep).isNotNull()
    }

    @Test
    fun `disabled plugin injects nothing`() {
        val project = project()
        project.extensions.getByType(JooqCodegenExtension::class.java).enabled.set(false)
        project.evaluate()

        assertAll {
            assertThat(
                project.configurations.getByName("implementation").dependencies
                    .find { it.group == "org.jooq" && it.name == "jooq" },
            ).isNull()
            assertThat(
                project.configurations.getByName("jooqCodegen").dependencies
                    .find { it.name == "jooq-liquibase-testcontainer" },
            ).isNull()
            assertThat(project.tasks.findByName("jooqCodegen")).isNull()
        }
    }

    @Test
    fun `plugin applies dema defaults and layers the consumer override`() {
        val project = project()
        val ext = project.extensions.getByType(JooqCodegenExtension::class.java)
        ext.databaseImage.set("postgresql:16-alpine")
        // object: Action is required here — a trailing lambda is ambiguous with the Gradle
        // Kotlin DSL Action() factory function in test scope.
        val dbAction = object : Action<DatabaseExtension> {
            override fun execute(db: DatabaseExtension) {
                db.includes = "custom_.*"
            }
        }
        val genAction = object : Action<GeneratorExtension> {
            override fun execute(gen: GeneratorExtension) {
                gen.database(dbAction)
            }
        }
        val cfgAction = object : Action<ConfigurationExtension> {
            override fun execute(config: ConfigurationExtension) {
                config.generator(genAction)
            }
        }
        ext.configuration(cfgAction)
        project.evaluate()

        val config = resolvedConfiguration(project)
        assertAll {
            assertThat(config.jdbc.url).isEqualTo("jdbc:tc:postgresql:16-alpine:///test-db")
            assertThat(config.generator.database.includes).isEqualTo("custom_.*")
            assertThat(config.generator.generate.nullableAnnotationType)
                .isEqualTo("org.jetbrains.annotations.Nullable")
        }
    }

    @Test
    fun `creates the jooq source set and leaves main untouched`() {
        val project = project()
        project.evaluate()

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val generatedDir = project.layout.buildDirectory.dir("generated/sources/jooq").get().asFile

        assertAll {
            assertThat(sourceSets.findByName("jooq")).isNotNull()
            assertThat(sourceSets.getByName("jooq").java.srcDirs.contains(generatedDir)).isTrue()
            assertThat(sourceSets.getByName("main").java.srcDirs.contains(generatedDir)).isFalse()
        }
    }

    @Test
    fun `compile tasks depend on jooqCodegen`() {
        val project = project()
        // The jooq-codegen module does not have the Kotlin plugin on its test classpath,
        // so register a stand-in task named `compileKotlin` instead of applying it.
        project.tasks.register("compileKotlin")
        project.evaluate()

        val compileKotlin = project.tasks.getByName("compileKotlin")
        val compileJooqJava = project.tasks.getByName("compileJooqJava")
        assertAll {
            assertThat(
                compileKotlin.taskDependencies.getDependencies(compileKotlin)
                    .any { it.name == "jooqCodegen" },
            ).isTrue()
            assertThat(
                compileJooqJava.taskDependencies.getDependencies(compileJooqJava)
                    .any { it.name == "jooqCodegen" },
            ).isTrue()
        }
    }
}
