package com.github.corelesser.core

import com.github.corelesser.core.materials.Item

class ItemStack<K: Item, V: Long>(
    private val max_value: V,
    private val max_size: Int = Integer.MAX_VALUE,
    private val pack: MutableMap<K, V> = mutableMapOf(),
): MutableMap<K, V> by pack {
    override fun put(key: K, value: V): V? {
        if (value > max_value) return pack[key]
        if (pack.size >= max_size && !pack.containsKey(key)) return pack[key]
        return pack.put(key, value)
    }
    override fun putAll(from: Map<out K, V>) {
        from.forEach { put(it.key, it.value) }
    }
    fun remaining_space(key: K): Long {
        return max_value - (pack[key] ?: 0L)
    }
}