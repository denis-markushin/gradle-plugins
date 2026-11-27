plugins {
    id("org.demalab.spotless")
}

spotless {
    kotlin {
        targetExclude("build-logic/*/build/**")
    }
}

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
