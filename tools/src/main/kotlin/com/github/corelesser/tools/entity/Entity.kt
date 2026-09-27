package com.github.corelesser.tools.entity

import com.github.corelesser.tools.Radius
import com.github.corelesser.tools.Vector2D

abstract class Entity(
    val id: Long,
    val rotation: Radius,
    var position: Vector2D
)