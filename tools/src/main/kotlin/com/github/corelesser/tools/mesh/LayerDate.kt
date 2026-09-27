package com.github.corelesser.tools.mesh

import kotlinx.serialization.Serializable

@Serializable
sealed class LayerDate(
    val colorDate: ColorDate?
)