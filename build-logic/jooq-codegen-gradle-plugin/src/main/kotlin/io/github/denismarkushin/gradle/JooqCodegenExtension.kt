package io.github.denismarkushin.gradle

import io.github.denismarkushin.gradle.jooq.VersionCatalog
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import org.jooq.codegen.gradle.MetaExtensions.ConfigurationExtension
import javax.inject.Inject

/**
 * Configuration surface for the standalone jOOQ codegen plugin.
 *
 * When the plugin is used directly, configure it via `demaJooq { }`.
 * When used through the `spring-service` plugin, configure it via `platform { jooq { } }`,
 * which mirrors its values into this extension.
 */
abstract class JooqCodegenExtension @Inject constructor(
    objects: ObjectFactory,
) {

    companion object {
        const val NAME = "demaJooq"
    }

    /** Master switch. When false, the plugin injects nothing and configures nothing. */
    val enabled: Property<Boolean> = objects.property<Boolean>().convention(true)

    /** Runtime `org.jooq:jooq` version. */
    val jooqVersion: Property<String> = objects.property<String>().convention(VersionCatalog.JOOQ_VERSION)

    /** Testcontainers database image used to build the codegen JDBC URL. */
    val databaseImage: Property<String> = objects.property<String>().convention("postgresql:17.5-alpine")

    /** Optional consumer override, layered on top of the dema defaults. */
    val configAction: Property<Action<ConfigurationExtension>> = objects.property()

    fun configuration(action: Action<ConfigurationExtension>) {
        configAction.set(action)
    }
}
