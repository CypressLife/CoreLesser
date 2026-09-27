package com.github.corelesser.tools.entity.ables

import com.github.corelesser.tools.Vector2D

interface Moveable {
    val acceleration: Float
    val deceleration: Float
    val speed_max: Float
    var speed: Float
    var target: Vector2D
}