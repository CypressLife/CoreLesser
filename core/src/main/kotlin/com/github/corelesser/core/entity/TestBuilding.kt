package com.github.corelesser.core.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.ables.Moveable
import com.github.corelesser.core.entity.ables.SpriteDrawable
import kotlin.math.PI

class TestEntity(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val acceleration: Float,
    override val deceleration: Float,
    override var speed: Float,
    override val speed_max: Float,
    override var target: Vector2D,
    override val texture: Texture,
): Entity(id,position,rotation), Moveable, SpriteDrawable {
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2, position.y - field.height / 2)
            field.rotation = rotation.value * 180f / PI.toFloat()
            return field
        }
}