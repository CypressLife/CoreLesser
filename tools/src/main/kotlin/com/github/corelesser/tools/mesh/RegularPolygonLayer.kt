package com.github.corelesser.tools.mesh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("regular-polygon")
data class RegularPolygonLayer(
    val sides: Int,
    val vector: Pair<Float, Float>,
    val center: Pair<Float, Float> = Pair(0f, 0f),
    val color: ColorDate
): LayerDate(color)
