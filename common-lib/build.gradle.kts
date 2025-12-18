plugins {
    alias(libs.plugins.buildconfig)
}

buildConfig {
    className("VersionCatalog")
    packageName(project.group.toString().replace("-", ""))
    useKotlinOutput()
    useKotlinOutput { internalVisibility = false }

    buildConfigField("JCABI_ASPECTS_DEP", libs.jcabiAspects.get().toString())
}
