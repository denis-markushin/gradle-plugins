package io.github.denismarkushin.gradle.configurator

import io.github.denismarkushin.gradle.JooqCodegenExtension
import io.github.denismarkushin.gradle.JooqCodegenPlugin
import io.github.denismarkushin.gradle.extension.DemaPlatformExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

private const val LIQUIBASE_STARTER = "org.springframework.boot:spring-boot-starter-liquibase"

/**
 * Applies [JooqCodegenPlugin] and mirrors `platform { jooq { } }` into its [JooqCodegenExtension].
 *
 * While jOOQ is on, `spring-boot-starter-liquibase` goes to `implementation`: jOOQ code is generated
 * from the Liquibase changelog, and Spring Boot 4 runs the migrations only with this starter.
 *
 * The plugin is applied eagerly (not inside an `afterEvaluate`) so that the plugin's own single
 * `afterEvaluate` is registered during normal project evaluation. Property-to-property `set` calls
 * stay lazy, so the consumer's `platform { }` block is still read correctly.
 *
 * `jooqVersion` and `databaseImage` fall back to the standalone extension's own conventions when
 * the consumer leaves the `platform` values unset; the fallback is captured eagerly as a value to
 * avoid a self-referential provider.
 */
internal fun Project.configureJooq() {
    val platform = extensions.getByType<DemaPlatformExtension>()

    plugins.apply(JooqCodegenPlugin::class)

    val jooqExt = extensions.getByType<JooqCodegenExtension>()
    jooqExt.enabled.set(platform.useJooq)
    jooqExt.jooqVersion.set(platform.jooq.jooqVersion.orElse(jooqExt.jooqVersion.get()))
    jooqExt.databaseImage.set(platform.jooq.databaseImage.orElse(jooqExt.databaseImage.get()))
    jooqExt.changelogFile.set(platform.jooq.changelogFile.orElse(jooqExt.changelogFile.get()))
    // configAction has no fallback; an unset property means "no consumer override"
    jooqExt.configAction.set(platform.jooq.configAction)
    val starter = platform.useJooq.map { if (it) listOf(dependencies.create(LIQUIBASE_STARTER)) else emptyList() }
    configurations.named("implementation") { dependencies.addAllLater(starter) }
}
