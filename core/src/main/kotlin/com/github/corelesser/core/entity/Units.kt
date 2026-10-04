package com.github.corelesser.core.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Vector2
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.ables.Moveable
import com.github.corelesser.core.entity.ables.SpriteDrawable
import com.github.corelesser.core.entity.ables.Storeable
import com.github.corelesser.core.materials.Item
import kotlin.math.PI
import kotlin.math.atan2

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

class Carrier(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val acceleration: Float,
    override val deceleration: Float,
    override var speed: Float,
    override val speed_max: Float,
    override var target: Vector2D,
    override val item_capacity: Long,
    override val texture: Texture
    ): Entity(id,position,rotation), Moveable, Storeable, SpriteDrawable {
    init {
        speed = 0f
    }

    override val item_store_list = mutableMapOf<Item, Long>()
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2, position.y - field.height / 2)
            field.rotation = rotation.value * 180f / PI.toFloat() - 90f
            return field
        }
    val  direction: Vector2D
        get() = target - position
    val distance: Float
        get() = direction.length()
    val  rotate: Radius
        get() = Radius.create(atan2(direction.y, direction.x))
    var arrive = false
    fun target(target: Vector2D) {
        arrive = false
        this.target = target
        rotation = rotate
    }
    fun move() {
        if (speed >= distance) {
            arrive = true
            position = target
        }
        if (arrive) {
            speed -= deceleration
            speed = maxOf(0f, speed)
        }
        else {
            speed += acceleration
            speed = minOf(speed, speed_max)
        }
        position += Vector2D.mole(speed, rotation)
    }
}