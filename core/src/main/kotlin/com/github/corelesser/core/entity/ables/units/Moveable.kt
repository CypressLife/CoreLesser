package com.github.corelesser.core.entity.ables.units

import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.Entity
import kotlin.math.pow

// 单位实体移动能力接口
interface Moveable {
    val acceleration: Float
    val deceleration: Float
    val rotate: Radius
    val speed_max: Float
    var speed: Float
    var target: Vector2D
}

// 单位实体移动处理类
abstract class MoveHelper {
    abstract fun entity_move(entity: Entity)
}

class UnitEntityMove: MoveHelper() {
    override fun entity_move(entity: Entity) {
        // 不是可移动直接跳过，同时使得传入的 entity 在后续使用中是 Moveable 的实现
        if (entity !is Moveable) return
        val displace = entity.target - entity.position
        val distance = displace.length()
        // 移动距离过小直接吸附，吸附完返回
        if (distance <= 0.002f) {
            entity.position = entity.target
            entity.speed = 0f
            return
        }
        // 计算应该加速还是减速
        val brake_distance =
            if (entity.deceleration > 0f)
                entity.speed.pow(2) / (2f * entity.deceleration)
            else Float.MAX_VALUE
        if (brake_distance > distance) {
            entity.speed = maxOf(0f, entity.speed - entity.deceleration)
        } else {
            entity.speed = minOf(entity.speed + entity.acceleration, entity.speed_max)
        }
        // 朝向目标
        val direction = displace.direction()
        val rotate: Radius = direction - entity.rotation
        when {
            rotate > +entity.rotate -> entity.rotation += entity.rotate
            rotate < -entity.rotate -> entity.rotation -= entity.rotate
            else -> entity.rotation = direction
        }
        // 移动至朝向位置，需要确保位移距离不超过实际距离
        val step = minOf(entity.speed, distance)
        entity.position += Vector2D.Companion.mole(step, entity.rotation)
        // 到达目标附近后吸附
        if (step >= distance) {
            entity.position = entity.target
            entity.speed = 0f
        }
    }
}