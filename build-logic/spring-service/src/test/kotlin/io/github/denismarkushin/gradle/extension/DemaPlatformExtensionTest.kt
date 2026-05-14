package io.github.denismarkushin.gradle.extension

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class DemaPlatformExtensionTest {

    private fun createExtension(): DemaPlatformExtension {
        val project = ProjectBuilder.builder().build()
        return project.extensions.create(DemaPlatformExtension.NAME, DemaPlatformExtension::class.java)
    }

    @Test
    fun `extension is created with correct name`() {
        val project = ProjectBuilder.builder().build()
        val ext = project.extensions.create(DemaPlatformExtension.NAME, DemaPlatformExtension::class.java)

        assertAll {
            assertThat(project.extensions.findByName("platform")).isNotNull()
            assertThat(ext).isNotNull()
        }
    }

    @Test
    fun `useKapt defaults to true`() {
        val ext = createExtension()

        assertThat(ext.useKapt.get()).isTrue()
    }

    @Test
    fun `useJcabi defaults to true`() {
        val ext = createExtension()

        assertThat(ext.useJcabi.get()).isTrue()
    }

    @Test
    fun `useMapstruct defaults to false`() {
        val ext = createExtension()

        assertThat(ext.useMapstruct.get()).isFalse()
    }

    @Test
    fun `spring useCloud defaults to false`() {
        val ext = createExtension()

        assertThat(ext.spring.useCloud.get()).isFalse()
    }

    @Test
    fun `netflix dgs useNetflixDgs defaults to false`() {
        val ext = createExtension()

        assertThat(ext.spring.netflixDgs.useNetflixDgs.get()).isFalse()
    }

    @Test
    fun `dgs generator defaults are correct`() {
        val ext = createExtension()
        val generator = ext.spring.netflixDgs.generator

        assertAll {
            assertThat(generator.generateDocs.get()).isTrue()
            assertThat(generator.generateClient.get()).isTrue()
            assertThat(generator.generateBoxedTypes.get()).isTrue()
            assertThat(generator.snakeCaseConstantNames.get()).isTrue()
            assertThat(generator.addGeneratedAnnotation.get()).isTrue()
            assertThat(generator.generateKotlinNullableClasses.get()).isTrue()
            assertThat(generator.generateKotlinClosureProjections.get()).isTrue()
        }
    }

    @Test
    fun `dgs generator typeMapping has default entries`() {
        val ext = createExtension()
        val typeMapping = ext.spring.netflixDgs.generator.typeMapping.get()

        assertAll {
            assertThat(typeMapping.containsKey("UUID")).isTrue()
            assertThat(typeMapping.containsKey("LocalDateTime")).isTrue()
            assertThat(typeMapping.containsKey("Upload")).isTrue()
        }
    }

    @Test
    fun `spring DSL allows configuration`() {
        val ext = createExtension()

        ext.spring {
            useCloud.set(true)
            netflixDgs {
                useNetflixDgs.set(true)
            }
        }

        assertAll {
            assertThat(ext.spring.useCloud.get()).isTrue()
            assertThat(ext.spring.netflixDgs.useNetflixDgs.get()).isTrue()
        }
    }

    @Test
    fun `extension properties are mutable`() {
        val ext = createExtension()

        ext.useKapt.set(false)
        ext.useJcabi.set(false)
        ext.useMapstruct.set(true)

        assertAll {
            assertThat(ext.useKapt.get()).isFalse()
            assertThat(ext.useJcabi.get()).isFalse()
            assertThat(ext.useMapstruct.get()).isTrue()
        }
    }
}
