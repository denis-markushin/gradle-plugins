package io.github.denismarkushin.gradle.extension

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import org.jooq.codegen.gradle.MetaExtensions.ConfigurationExtension
import javax.inject.Inject

/**
 * `platform { jooq { } }` configuration. Mirrored into the standalone
 * `JooqCodegenExtension` by [io.github.denismarkushin.gradle.configurator.configureJooq].
 */
abstract class JooqHandler @Inject constructor(
    objects: ObjectFactory,
) {
    val jooqVersion: Property<String> = objects.property()
    val databaseImage: Property<String> = objects.property()
    val configAction: Property<Action<ConfigurationExtension>> = objects.property()

    fun configuration(action: Action<ConfigurationExtension>) {
        configAction.set(action)
    }
}
