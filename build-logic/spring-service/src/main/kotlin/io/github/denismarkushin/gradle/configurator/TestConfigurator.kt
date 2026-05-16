package io.github.denismarkushin.gradle.configurator

import io.github.denismarkushin.gradle.VersionCatalog
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.withType

internal fun Project.configureTestTask() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            showStandardStreams = false
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    dependencies.add("testRuntimeOnly", VersionCatalog.JUNIT_LAUNCHER_DEP)
    dependencies.add("testImplementation", VersionCatalog.SPRING_BOOT_TEST_DEP)
    dependencies.add("testImplementation", VersionCatalog.ASSERTK_DEP)
    dependencies.add("testImplementation", VersionCatalog.SPRINGMOCK_DEP)
    dependencies.add("testImplementation", VersionCatalog.KOTEST_DEP)
    dependencies.add("testImplementation", VersionCatalog.AWAITILITY_DEP)

    configurations.matching { it.name.startsWith("test") }.configureEach {
        exclude(group = "org.mockito")
    }
}
