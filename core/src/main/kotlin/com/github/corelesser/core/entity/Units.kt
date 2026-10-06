package com.github.corelesser.core.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.ables.units.Carrierable
import com.github.corelesser.core.entity.ables.units.Moveable
import com.github.corelesser.core.entity.ables.SpriteDrawable
import com.github.corelesser.core.entity.ables.buildings.P2Pchannel
import com.github.corelesser.core.entity.ables.buildings.Storeable
import com.github.corelesser.core.materials.Item
import kotlin.math.PI

// 抽象单位实体类
abstract class UnitEntity(
    id: Long,
    position: Vector2D,
    rotation: Radius,
): Entity(id, position, rotation)

class TestEntity(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val acceleration: Float,
    override val deceleration: Float,
    override val rotate: Radius,
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

class TestCarrier(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val acceleration: Float,
    override val deceleration: Float,
    override val rotate: Radius,
    override val speed_max: Float,
    override var target: Vector2D,
    override val item_capacity: Long,
    override var channel: P2Pchannel?,
    override val texture: Texture
    ): UnitEntity(id,position,rotation), Moveable, Carrierable, SpriteDrawable {
    override var speed: Float = 0f
    init {
        speed = 0f
    }
    override var now_item_type: Item? = null
    override var now_item_value: Long = 0L
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2, position.y - field.height / 2)
            field.rotation = rotation.value * 180f / PI.toFloat() - 90f
            return field
    }
}