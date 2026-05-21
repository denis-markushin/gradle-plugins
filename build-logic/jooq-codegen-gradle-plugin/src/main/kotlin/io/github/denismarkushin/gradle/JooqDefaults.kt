package io.github.denismarkushin.gradle

import org.jooq.codegen.gradle.MetaExtensions.ConfigurationExtension
import org.jooq.meta.jaxb.Logging

/**
 * Populates a jOOQ [ConfigurationExtension] with the dema platform defaults.
 *
 * @param databaseImage testcontainers image, e.g. `postgresql:17.5-alpine`
 * @param changelogFile absolute path to the Liquibase master changelog
 * @param targetDir absolute path the generated sources are written to
 * @param packageName root package for generated classes
 */
internal fun ConfigurationExtension.applyDemaDefaults(
    databaseImage: String,
    changelogFile: String,
    targetDir: String,
    packageName: String,
) {
    logging = Logging.DEBUG

    jdbc {
        driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
        url = "jdbc:tc:$databaseImage:///test-db"
    }

    generator {
        database {
            name = "org.dema.jooq.liquibase.LiquibasePostgresTcDatabase"
            includes = ".*"
            excludes = "databasechangelog|databasechangeloglock"
            inputSchema = "public"
            properties {
                property {
                    key = "liquibaseChangelogFile"
                    value = changelogFile
                }
            }
            forcedTypes {
                forcedType {
                    userType = "java.time.LocalDateTime"
                    includeTypes = "(?i)(timestamp(\\(\\d+\\))?\\s*with\\s*time\\s*zone|timestamptz)"
                }
            }
        }
        generate {
            isPojos = false
            isDaos = false
            isRecordsImplementingRecordN = false
            isNullableAnnotation = true
            isNonnullAnnotation = true
            nullableAnnotationType = "org.jetbrains.annotations.Nullable"
            nonnullAnnotationType = "org.jetbrains.annotations.NotNull"
            isValidationAnnotations = true
        }
        target {
            directory = targetDir
            this.packageName = packageName
        }
    }
}
