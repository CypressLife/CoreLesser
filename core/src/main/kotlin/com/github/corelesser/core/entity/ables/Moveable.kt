package com.github.corelesser.core.entity.ables

import com.github.corelesser.core.Vector2D

interface Moveable {
    val acceleration: Float
    val deceleration: Float
    val speed_max: Float
    var speed: Float
    var target: Vector2D
}