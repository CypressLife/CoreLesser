package com.github.corelesser.tools.mesh

import kotlinx.serialization.json.Json

class ShapeDrawer {
    companion object{
        val json = Json { ignoreUnknownKeys = true }
        //val shape = json.decodeFromString<ShapeDate>()
    }
}