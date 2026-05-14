plugins {
    id("io.github.denis-markushin.spotless")
}

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
