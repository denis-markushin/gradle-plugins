package io.github.denismarkushin.gradle.configurator

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Applies Kover and builds its XML (JaCoCo schema) and log reports on `check`, which is what the
 * shared CI template runs; GitLab reads the XML as `coverage_format: jacoco` and takes the
 * percentage from the log line. Generated code is excluded: DGS marks its classes with a
 * binary-retention `Generated`, jOOQ with `org.jooq.Generated` (enabled by the jooq-codegen
 * defaults), while MapStruct's `javax.annotation.processing.Generated` has source retention and
 * is invisible in bytecode, hence the class patterns.
 */
internal fun Project.configureKover() {
    plugins.apply("org.jetbrains.kotlinx.kover")
    extensions.configure<KoverProjectExtension> {
        reports {
            filters {
                excludes {
                    annotatedBy("*Generated")
                    classes("*Impl", "*ConversionServiceAdapter")
                }
            }
            total {
                xml { onCheck.set(true) }
                log { onCheck.set(true) }
            }
        }
    }
}
