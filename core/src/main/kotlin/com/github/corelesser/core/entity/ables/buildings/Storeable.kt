package com.github.corelesser.core.entity.ables.buildings

import com.github.corelesser.core.entity.manager.EntityManager
import com.github.corelesser.core.materials.Item

// 建筑实体存储能力接口
interface Storeable {
    /*
     * 物品容量
     * 物品列表
     */
    val item_capacity: Long
    val item_store_list: MutableMap<Item, Long>
    fun can_extract(item: Item): Boolean = true
}

// 建筑实体存储处理类
class StoreHelper(private val entity_manager: EntityManager) {
    // val store_entities = mutableMapOf<Long, Storeable>()
    fun accept_item(acceptor: Storeable, item: Item, value: Long): Long {
        if (value <= 0L) return value
        val now_value = acceptor.item_store_list[item] ?: 0L
        val remaining_value = acceptor.item_capacity - now_value
        if (remaining_value <= 0) return value
        val add_value = minOf(value, remaining_value)
        acceptor.item_store_list[item] = now_value + add_value
        return value - add_value
    }
}