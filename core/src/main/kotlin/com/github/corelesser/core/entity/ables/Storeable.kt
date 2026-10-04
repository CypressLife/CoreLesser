package com.github.corelesser.core.entity.ables

import com.github.corelesser.core.entity.manager.EntityManager
import com.github.corelesser.core.materials.Item

interface Storeable {
    val item_capacity: Long
    val item_store_list: MutableMap<Item, Long>
}

class StoreHelper(private val entity_manager: EntityManager) {
    val store_entities = mutableMapOf<Long, Storeable>()
    init {
        for (i: Long in 0 until entity_manager.entities.size.toLong()) {
            if (entity_manager.entities[i] is Storeable)
                store_entities[i] = entity_manager.entities[i] as Storeable
        }
    }
    fun update_entities() {
        for (i: Long in 0 until entity_manager.entities.size.toLong()) {
            if (entity_manager.entities[i] is Storeable)
                store_entities[i] = entity_manager.entities[i] as Storeable
        }
    }
    fun update(pusher: Storeable, acceptor: Storeable) {

    }
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