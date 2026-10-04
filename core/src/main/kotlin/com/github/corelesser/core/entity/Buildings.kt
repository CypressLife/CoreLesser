package com.github.corelesser.core.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.ables.SpriteDrawable
import com.github.corelesser.core.entity.ables.Storeable
import com.github.corelesser.core.materials.Item
import kotlin.math.PI

object Buildings {
    fun create_store(
        id: Long,
        position: Vector2D,
        rotation: Radius,
        item_capacity: Long,
        texture: Texture,
    ): TestStore {
        return TestStore(
            id = id,
            position = position,
            rotation = rotation,
            item_capacity = item_capacity,
            texture = texture,
        )
    }
}

class TestStore(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val item_capacity: Long,
    override val texture: Texture,
): Entity(id, position, rotation), Storeable, SpriteDrawable {
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2f, position.y - field.height / 2f)
            field.rotation = rotation.value * 180f / PI.toFloat()
            return field
        }
    override val item_store_list = mutableMapOf<Item, Long>()
}