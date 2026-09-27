package com.github.corelesser.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.github.corelesser.app.CoreLesser

fun main() {
    Lwjgl3Application(CoreLesser(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("CoreLesser-build-1.0")
        setWindowIcon("Icon-256.png")
        setWindowedMode(1280, 720)
        setBackBufferConfig(8, 8, 8, 8, 16, 0, 2)
        setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2)
        setTransparentFramebuffer(true)
        setInitialBackgroundColor(Color(0f, 0f, 0f, 0f))
    })
}
