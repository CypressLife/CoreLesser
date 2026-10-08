package com.github.corelesser.core.entity

object EntityCreator {
    var next_ID = Long.MIN_VALUE
        get() {
            return field + 1
        }
    fun create_carrier() {}
}