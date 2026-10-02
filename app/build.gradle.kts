plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    //id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinJVM)
    alias(libs.plugins.kotlinPluginSerialization)
    // Apply the Application plugin to add support for building an executable JVM application.
}

dependencies {
    // Project "app" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
    implementation(project(":core"))
    api(libs.bundles.kotlinxEcosystem)
    api(libs.bundles.gdxLibs)
    api(libs.bundles.ktxLibs)
    api(libs.bundles.visUiLibs)
    api(libs.bundles.colorful)
}