package com.github.corelesser.core.entity

import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D

abstract class Entity(
    val id: Long,
    var position: Vector2D,
    var rotation: Radius
)