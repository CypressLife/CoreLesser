package com.github.corelesser.core.mesh

import kotlinx.serialization.Serializable

@Serializable
sealed class LayerDate(
    val colorDate: ColorDate?
)