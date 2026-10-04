package com.github.corelesser.core.entity.manager

import com.github.corelesser.core.entity.Entity

class EntityManager {
    val entities = mutableMapOf<Long, Entity>()
    fun add_entity(entity: Entity) {
        entities[entity.id] = entity
    }
    fun find_entity(id: Long) {
        entities[id] ?: println("找不到 $id id 的实体，可能未生成或已删除")
    }
    fun remove_entity(id: Long) {
        entities.remove(id) ?: println("找不到 $id id 的实体，可能未生成或已删除")
    }
}