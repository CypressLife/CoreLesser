package com.github.corelesser.core.mesh

import kotlinx.serialization.Serializable

@Serializable
data class ShapeDate(
    val name: String,
    val width: Float,
    val height: Float,
    val layers: List<LayerDate>
)