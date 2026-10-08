package com.github.corelesser.core.entity.ables.buildings

import com.github.corelesser.core.entity.BuildingEntity
import com.github.corelesser.core.materials.Item

// 建筑实体工厂能力接口
interface Factoriable: Storeable {
    val recipe: Recipe
    var progress: Float
    var active: Boolean
    override fun can_extract(item: Item): Boolean {
        return recipe.item_output[item] != null
    }
    override fun item_list_print(): String {
        var text = ""
        for (item in recipe.item_input.keys) {
            text += "${item.display_name}: ${item_store_list[item] ?: 0L} / $item_capacity\n"
        }
        for (item in recipe.item_output.keys) {
            text += "${item.display_name}: ${item_store_list[item] ?: 0L} / $item_capacity\n"
        }
        return text
    }
}

// 工厂能力状态处理
class FactoryController {
    fun update(factories: List<Factoriable>, tick: Float) {
        for (factor in factories) {
            update_one(factor, tick)
        }
    }
    private fun update_one(factory: Factoriable, tick: Float) {
        if (factory !is BuildingEntity) return
        if (!factory.active) return
        if (!can_consume(factory) || !can_output(factory)) return
        factory.progress += tick
        if (factory.progress < factory.recipe.time) return/*
        if (factory.progress >= factory.recipe.time) {
            factory.progress = 0f
            for ((item, output) in factory.recipe.item_output) {
                factory.item_store_list[item]?.let { factory.item_store_list[item] = it + output }
            }
        }*/
        for ((item, input) in factory.recipe.item_input) {
            factory.item_store_list[item] = (factory.item_store_list[item] ?: 0L) - input
        }
        for ((item, output) in factory.recipe.item_output) {
            factory.item_store_list[item] = (factory.item_store_list[item] ?: 0L) + output
        }
        factory.progress = 0f
    }
    private fun can_consume(factory: Factoriable): Boolean {
        for ((item, input) in factory.recipe.item_input) {
            if ((factory.item_store_list[item] ?: 0L) < input) return false
        }
        return true
    }
    private fun can_output(factory: Factoriable): Boolean {
        for ((item, value) in factory.recipe.item_output) {
            if ((factory.item_store_list[item] ?: 0L) + value > factory.item_capacity) return false
        }
        return true
    }
}

// 配方类
data class Recipe(
    val time: Float,
    val item_input: Map<Item, Long>,
    val item_output: Map<Item, Long>,
)