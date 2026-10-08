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
    fun item_list_print(): String {
        var text = ""
        for (item in item_store_list.keys) {
            text += "${item.display_name}: ${item_store_list[item]} / $item_capacity\n"
        }
        return text
    }
}

// 建筑实体存储处理类
class StoreHelper(private val entity_manager: EntityManager) {
}