package com.github.corelesser.core.entity.ables.buildings

import com.github.corelesser.core.entity.Entity
import com.github.corelesser.core.entity.ables.units.CarrierController
import com.github.corelesser.core.entity.ables.units.CarrierHelper
import com.github.corelesser.core.entity.ables.units.Carrierable
import com.github.corelesser.core.entity.ables.units.MoveHelper
import com.github.corelesser.core.materials.Item

// 单位实体运输控制中心接口
interface CarrierCenterable {
    val carriers: MutableMap<Long, Carrierable>
    val channels: MutableList<P2Pchannel>
    fun update(tick: Float)
}

// 控制中心更新类
class CarrierCenterHelper {
    fun update(
        centers: List<CarrierCenterable>,
        carrier_controllers: CarrierController,
        carrier_helper: CarrierHelper,
        move_helper: MoveHelper,
        tick: Float
    ) {
        for (center in centers) {
            carrier_controllers.update(center.carriers.values.toList(), carrier_helper, tick)
            for (carrier in center.carriers.values) {
                move_helper.entity_move(carrier as Entity)
            }
        }
    }
}

// P2P通道类
data class P2Pchannel(
    val pickup: Storeable,
    val drop: Storeable,
    val item: Item,
    var full_load: Boolean = false
)