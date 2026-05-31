package io.github.denismarkushin.gradle.extension

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class DemaPlatformExtension @Inject constructor(
    objects: ObjectFactory,
) {

    companion object {
        const val NAME = "platform"
        internal fun Project.thePlatform(): DemaPlatformExtension = extensions.create(NAME, DemaPlatformExtension::class.java)
    }

    val useKapt: Property<Boolean> = objects.property<Boolean>().convention(true)
    val useJcabi: Property<Boolean> = objects.property<Boolean>().convention(true)
    val useMapstruct: Property<Boolean> = objects.property<Boolean>().convention(false)
    val useJooq: Property<Boolean> = objects.property<Boolean>().convention(true)
    val jooq: JooqHandler = objects.newInstance<JooqHandler>()

    fun jooq(action: Action<JooqHandler>) {
        action.execute(jooq)
    }

    val spring: SpringHandler = objects.newInstance<SpringHandler>()

    fun spring(action: Action<SpringHandler>) {
        action.execute(spring)
    }
}

abstract class SpringHandler @Inject constructor(
    objects: ObjectFactory,
) {
    val useCloud: Property<Boolean> = objects.property<Boolean>().convention(false)
    val netflixDgs: NetflixDgsHandler = objects.newInstance<NetflixDgsHandler>()

    fun netflixDgs(action: Action<NetflixDgsHandler>) {
        action.execute(netflixDgs)
    }
}

abstract class NetflixDgsHandler @Inject constructor(
    objects: ObjectFactory,
) {
    val useNetflixDgs: Property<Boolean> = objects.property<Boolean>().convention(false)
    val generator = objects.newInstance<DgsGeneratorHandler>()

    fun generator(action: Action<DgsGeneratorHandler>) {
        action.execute(generator)
    }
}

abstract class DgsGeneratorHandler @Inject constructor(
    objects: ObjectFactory,
) {
    companion object {
        private val DEFAULT_TYPE_MAPPINGS = mapOf(
            "UUID" to "java.util.UUID",
            "Generated" to "jakarta.annotation.Generated",
            "LocalDateTime" to "java.time.LocalDateTime",
            "Upload" to "org.springframework.web.multipart.MultipartFile",
            "ErrorInterface" to "org.dema.graphql.dgs.error.ErrorInterface",
            "NotFoundError" to "org.dema.graphql.dgs.error.NotFoundError",
            "ValidationError" to "org.dema.graphql.dgs.error.ValidationError",
            "ConflictError" to "org.dema.graphql.dgs.error.ConflictError",
            "UnauthorizedError" to "org.dema.graphql.dgs.error.UnauthorizedError",
            "ForbiddenError" to "org.dema.graphql.dgs.error.ForbiddenError",
            "ServiceUnavailableError" to "org.dema.graphql.dgs.error.ServiceUnavailableError",
            "RuntimeError" to "org.dema.graphql.dgs.error.RuntimeError",
        )
    }

    val generateDocs = objects.property<Boolean>().convention(true)
    val generateClient = objects.property<Boolean>().convention(true)
    val generateBoxedTypes = objects.property<Boolean>().convention(true)
    val snakeCaseConstantNames = objects.property<Boolean>().convention(true)
    val addGeneratedAnnotation = objects.property<Boolean>().convention(true)
    val generateKotlinNullableClasses = objects.property<Boolean>().convention(true)
    val generateKotlinClosureProjections = objects.property<Boolean>().convention(true)
    val typeMapping = objects.mapProperty<String, String>().apply { putAll(DEFAULT_TYPE_MAPPINGS) }
}
