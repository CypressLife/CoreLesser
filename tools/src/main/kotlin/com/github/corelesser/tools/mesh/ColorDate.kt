package com.github.corelesser.tools.mesh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorDate(
    val h: Float,
    val s: Float,
    val v: Float,
    val a: Float
)
