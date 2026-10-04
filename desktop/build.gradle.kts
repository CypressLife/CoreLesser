plugins {
    application
    alias(libs.plugins.kotlinJVM)
}

dependencies {
    implementation(project(":app"))
    api(libs.gdxCore)
    api(libs.gdxLwjgl3)
    api(libs.gdxPlatformDesktop) {
        artifact { classifier = "natives-desktop" }
    }
    api("com.badlogicgames.gdx:gdx-freetype-platform:${libs.versions.gdx.get()}:natives-desktop")
}

application {
    mainClass.set("com.github.corelesser.desktop.DesktopLauncherKt")
}