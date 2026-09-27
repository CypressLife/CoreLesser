package com.github.corelesser.tools.mesh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("circle")
data class CircleLayer(
    val center: Pair<Float, Float>,
    val radius: Float,
    val side: Int,
    val sides: Int = 32 + side,
    val color: ColorDate? = null
): LayerDate(color)
