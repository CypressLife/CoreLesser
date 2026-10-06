package com.github.corelesser.core.entity.ables.units

import com.github.corelesser.core.entity.BuildingEntity
import com.github.corelesser.core.entity.UnitEntity
import com.github.corelesser.core.entity.ables.buildings.Factoriable
import com.github.corelesser.core.entity.ables.buildings.P2Pchannel
import com.github.corelesser.core.entity.ables.buildings.Storeable
import com.github.corelesser.core.materials.Iron
import com.github.corelesser.core.materials.Item

// 单位实体运输能力接口
interface Carrierable {
    /*
     * 容量
     * 持有的物品类型
     * 持有的物品数量
     */
    val item_capacity: Long
    var now_item_type: Item?
    var now_item_value: Long
    var channel: P2Pchannel?
}

// 运输能力状态机，负责反馈运输状态
enum class CarrierState {
    Idle,
    MoveToPickup,
    Picking,
    MoveToDrop,
    Dropping,
    Waiting,
}

// 运输能力状态处理
class CarrierController {
    // 实体状态列表
    private val states = mutableMapOf<Long, CarrierState>()
    // 等待后的下一步
    private val waitings = mutableMapOf<Long, CarrierState>()
    // 实体倒计时列表
    private val timers = mutableMapOf<Long, Float>()
    // 实体装货所需时间
    private val action_time = 0.5f
    // 实体停止允许量
    private val arrive_length = 2f
    // 按输入列表更新
    fun update(carriers: List<Carrierable>, helper: CarrierHelper, tick: Float) {
        for (carrier in carriers) {
            update_one(carrier, helper, tick)
        }
    }
    // 按照状态机更新
    private fun update_one(carrier: Carrierable, helper: CarrierHelper, tick: Float) {
        if (carrier !is UnitEntity) return
        if (carrier !is Moveable) return
        val state = states.getOrPut(carrier.id) { CarrierState.Idle }
        val waiting = waitings[carrier.id] ?: CarrierState.Idle
        when (state) {
            /*
             * 空闲状态
             * 用于进行状态切换的判断
             */
            CarrierState.Idle -> {
                // 如果持有货品就去卸载
                if (carrier.now_item_value > 0L) {
                    carrier.target = (carrier.channel!!.drop as BuildingEntity).position
                    states[carrier.id] = CarrierState.MoveToDrop
                } else {
                    carrier.target = (carrier.channel!!.pickup as BuildingEntity).position
                    states[carrier.id] = CarrierState.MoveToPickup
                }
            }
            CarrierState.MoveToPickup -> {
                // 如果移动到取货点则取货
                if (arrived(carrier)) {
                    states[carrier.id] = CarrierState.Picking
                    timers[carrier.id] = action_time
                }
            }
            CarrierState.Picking -> {
                // 如果没货则计时器重置并等待
                if (!helper.can_pickup(carrier.channel!!.pickup, carrier.channel!!.item)) {
                    states[carrier.id] = CarrierState.Waiting
                    waitings[carrier.id] = CarrierState.Picking
                    timers[carrier.id] = action_time
                    return
                }
                // 取货要等待一段时间
                timers[carrier.id] = (timers[carrier.id] ?: action_time) - tick
                if (timers[carrier.id]!! <= 0f) {
                    // 已经有货则装货
                    if (carrier.now_item_value > 0L ) {
                        // 如果 P2P 要求装满则装满并且建筑必须是工厂
                        if (carrier.channel!!.full_load && carrier.channel!!.pickup is Factoriable) {
                            if (carrier.now_item_value >= carrier.item_capacity) {
                                carrier.target = (carrier.channel!!.drop as BuildingEntity).position
                                states[carrier.id] = CarrierState.MoveToDrop
                            } else {
                                // 装不满就等待
                                states[carrier.id] = CarrierState.Waiting
                                waitings[carrier.id] = CarrierState.Picking
                            }
                        } else {
                            // 否则任意一种装上就走
                            carrier.target = (carrier.channel!!.drop as BuildingEntity).position
                            states[carrier.id] = CarrierState.MoveToDrop
                        }
                    // 否则等待并重置计时器 (理论上不需要这步但为了严谨还是写上)
                    } else {
                        timers[carrier.id] = action_time
                    }
                }
            }
            CarrierState.MoveToDrop -> {
                // 如果移动到卸货点则卸货
                if (arrived(carrier)) {
                    states[carrier.id] = CarrierState.Dropping
                    // 重置等待时长
                    timers[carrier.id] = action_time
                }
            }
            CarrierState.Dropping -> {
                // 卸货同样需要时间
                timers[carrier.id] = (timers[carrier.id] ?: action_time) - tick
                // 卸货后状态转为空闲
                if (timers[carrier.id]!! <= 0f) {
                    val item_moved = helper.drop(carrier, carrier.channel!!.drop)
                    if (item_moved == 0L) {
                        // 没卸货成功则等待
                        states[carrier.id] = CarrierState.Waiting
                        waitings[carrier.id] = CarrierState.Dropping
                        timers[carrier.id] = action_time
                    } else {
                        states[carrier.id] = CarrierState.Idle
                        timers[carrier.id] = action_time
                    }
                }
            }
            CarrierState.Waiting -> {
                // 等待倒计时
                timers[carrier.id] = (timers[carrier.id] ?: 0f) - tick
                if (timers[carrier.id]!! <= 0f) {
                    timers[carrier.id] = action_time
                    states[carrier.id] = waiting
                }
            }
        }
    }
    // 判断是否到达
    private fun arrived(carrier: Moveable): Boolean {
        if (carrier !is UnitEntity) return false
        return (carrier.target - carrier.position).length() < arrive_length
    }
}

// 单位实体存储处理类
class CarrierHelper {
    /*
     * 实体装载
     * 从 building 装入 carrier
     * 返回实际装入数量，没装成返回 0
     */
    fun pickup(
        carrier: Carrierable,
        building: Storeable,
        item: Item,
    ): Long {
        // 处理异常搬运值
        if (carrier.item_capacity <= 0L) return 0L
        // 如果已经有物品则不能混装
        if (carrier.now_item_value > 0L && carrier.now_item_type != item) return 0L
        // 标记从建筑拿取的物品数量
        val building_item_value = building.item_store_list[item] ?: 0L
        if (building_item_value <= 0L) return 0L
        // 标记运输者的剩余空间
        val carrier_space = carrier.item_capacity - carrier.now_item_value
        if (carrier_space <= 0L) return 0L
        // 应该装取的物品数量
        val item_moved = minOf(building_item_value, carrier_space)
        // 不可以装入滚木
        if (item_moved <= 0L) return 0L
        // 此时排查完全部可能的异常，开始装载
        carrier.now_item_type = item
        carrier.now_item_value += item_moved
        building.item_store_list[item] = building_item_value - item_moved
        return item_moved
    }
    /*
     * 实体卸载
     * 从 carrier 装入 building
     * 返回实际卸出数量，没卸成返回 0
     */
    fun drop(
        carrier: Carrierable,
        building: Storeable,
    ): Long {
        // 你不能卸出滚木
        if (carrier.now_item_value <= 0L) return 0L
        // 标记卸载的物品类型
        val item = carrier.now_item_type ?: return 0L
        // 标记建筑中对应的物品数量
        val building_item_value = building.item_store_list[item] ?: 0L
        // 建筑剩余空间
        val building_space = building.item_capacity - building_item_value
        // 显然你不能装入超出容量的物品
        if (building_space <= 0L) return 0L
        // 应卸出的物品数量
        val moved = minOf(carrier.now_item_value, building_space)
        if (moved <= 0L) return 0L
        building.item_store_list[item] = building_item_value + moved
        carrier.now_item_value -= moved
        if (carrier.now_item_value == 0L) carrier.now_item_type = null
        return moved
    }
    /*
     * 货物剩余量检查
     * 用于空闲状态等待取货
     */
    fun can_pickup(building: Storeable, item: Item): Boolean {
        if (!building.can_extract(item)) return false
        return (building.item_store_list[item] ?: 0L) > 0L
    }
}