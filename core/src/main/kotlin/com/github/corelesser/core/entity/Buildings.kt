package com.github.corelesser.core.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.ables.SpriteDrawable
import com.github.corelesser.core.entity.ables.buildings.*
import com.github.corelesser.core.entity.ables.units.Carrierable
import com.github.corelesser.core.materials.Item
import kotlin.math.PI

// 抽象建筑实体类
abstract class BuildingEntity(
    id: Long,
    position: Vector2D,
    rotation: Radius,
): Entity(id, position, rotation)

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
): BuildingEntity(id, position, rotation), Storeable, SpriteDrawable {
    override val display_name: String? = "测试仓库"
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2f, position.y - field.height / 2f)
            field.rotation = rotation.value * 180f / PI.toFloat()
            return field
        }
    override val item_store_list = mutableMapOf<Item, Long>()
}

class TestCarrierCenter(
    id: Long,
    position: Vector2D,
    rotation: Radius,
): BuildingEntity(id, position, rotation), CarrierCenterable {
    override val display_name: String? = "测试中枢"
    override val carriers = mutableMapOf<Long, Carrierable>()
    override val channels = mutableListOf<P2Pchannel>()
    override fun update(tick: Float) {
    }
}

class TestFactory(
    id: Long,
    position: Vector2D,
    rotation: Radius,
    override val item_capacity: Long,
    override val recipe: Recipe,
    override val texture: Texture,
): BuildingEntity(id, position, rotation), Factoriable, SpriteDrawable {
    override val display_name: String? = "测试工厂"
    override val sprite = Sprite(texture)
        get() {
            field.setPosition(position.x - field.width / 2f, position.y - field.height / 2f)
            field.rotation = rotation.value * 180f / PI.toFloat()
            return field
        }
    override val item_store_list = mutableMapOf<Item, Long>()
    override var progress: Float = 0f
    override var active: Boolean = true
}