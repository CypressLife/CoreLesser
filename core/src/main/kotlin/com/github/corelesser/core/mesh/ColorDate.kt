package com.github.corelesser.core.mesh

import kotlinx.serialization.Serializable

@Serializable
data class ColorDate(
    val h: Float,
    val s: Float,
    val v: Float,
    val a: Float
)
