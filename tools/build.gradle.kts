plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    //id("buildsrc.convention.kotlin-jvm") version "2.3.10"
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinJVM)
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.gdxLibs)
    implementation(libs.bundles.ktxLibs)
    //implementation(libs.bundles.visUiLibs)
    implementation(libs.bundles.colorful)
}