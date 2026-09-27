package com.github.corelesser.tools.mesh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("polygon")
data class PolygonLayer(
    val vectors: List<Pair<Float, Float>>,
    val color: ColorDate? = null
): LayerDate(color)
