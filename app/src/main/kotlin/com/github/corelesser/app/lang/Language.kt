package com.github.corelesser.app.lang

abstract class LanguageText {
    abstract val title: String
    abstract val battle: String
    abstract val option: String
    abstract val copper: String
    abstract val iron: String
    abstract val gold: String
}

enum class Language(
    val code: String,
    val display_name: String,
    val text: LanguageText
) {
    Simple_Chinese("zh_cn", "简体中文", SimpleChinese),
    English("en_UK", "English", SimpleChinese)
}