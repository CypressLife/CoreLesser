package com.github.corelesser.app.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.github.corelesser.app.lang.Language
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle
import sun.font.TextLabel

// 字符构造器
object FontCreate {
    // 基准字号
    const val BASE_SIZE = 96

    // 语言字体实例
    lateinit var chinese_font: BitmapFont
        private set
    // 生成器引用
    private lateinit var generator: FreeTypeFontGenerator

    // 初始化
    fun initialized() {
        chinese_font = simple_chinese()
        VisUI.getSkin().add("default-font", chinese_font, BitmapFont::class.java)
    }

    // 加载中文
    fun simple_chinese() : BitmapFont {
        generator = FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/SourceHanSansCN-Light.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = BASE_SIZE
            incremental = true
            // 缩放时线性过滤是必须的，否则边缘会有锯齿或硬块
            minFilter = Texture.TextureFilter.Linear
            magFilter = Texture.TextureFilter.Linear
        }
        chinese_font = generator.generateFont(parameter)
        return chinese_font
    }

    // UI 字体文本设置
    fun Label.text(text: String, language: Language, size: Int): Label {
        val font = when (language) {
            Language.Simple_Chinese -> chinese_font
            else -> {chinese_font}
        }
        style = Label.LabelStyle(font, Color.WHITE)
        setText(text)
        setFontScale(size.toFloat() / BASE_SIZE)
        return this
    }
    fun VisLabel.text(text: String, language: Language, size: Int): VisLabel {
        val font = when (language) {
            Language.Simple_Chinese -> chinese_font
            else -> {chinese_font}
        }
        style = Label.LabelStyle(font, Color.WHITE)
        setText(text)
        setFontScale(size.toFloat() / BASE_SIZE)
        return this
    }
    fun VisTextButton.text(text: String, language: Language, size: Int = 1): VisTextButton {
        val font = when(language) {
            Language.Simple_Chinese -> chinese_font
            else -> {chinese_font}
        }
        style = VisTextButtonStyle(VisUI.getSkin().get(VisTextButtonStyle::class.java)).apply {
            this.font = font
        }
        label.style = Label.LabelStyle(font, Color.WHITE)
        label.setText(text)
        label.setFontScale(size.toFloat() / BASE_SIZE)
        return this
    }
}