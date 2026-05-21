pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(
    "jcabi-gradle-plugin",
    "jooq-codegen-gradle-plugin",
    "kotlin",
    "kotlin-library",
    "spotless",
    "spring-service",
)
