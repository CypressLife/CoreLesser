package com.github.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.corelesser.app.assets.FontCreate
import com.github.corelesser.app.assets.FontCreate.text
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.TestFactory
import com.github.corelesser.core.entity.ables.buildings.FactoryController
import com.github.corelesser.core.entity.ables.buildings.Recipe
import com.github.corelesser.core.materials.Iron
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.alpha
import ktx.app.clearScreen
import org.w3c.dom.Text

class UITestScreen(private val game: CoreLesser): CoreLesserScreen(game) {
    // 事件监听器
    val multiplexer = InputMultiplexer()
    // 游戏相机
    val world_camera = OrthographicCamera().apply {
        zoom = 2f
    }
    // 游戏视口
    val world_viewport = ExtendViewport(1280f, 720f, world_camera)
    // UI 相机
    val control_camera = OrthographicCamera()
    // UI 视口
    val control_viewport = ScreenViewport(control_camera)
    val building1 = TestFactory(
        1L,
        Vector2D.pair(128f, 128f),
        Radius.create(0f),
        item_capacity = 20L,
        recipe = Recipe(2f, mapOf(), mapOf(Iron to 1L)),
        texture = Texture("Factory.png")
    )
    val factory_helper = FactoryController()
    // 一个表示世界的舞台
    val world_stage = Stage(world_viewport)
    // 一个展示信息的舞台
    val control_stage = Stage(control_viewport)
    // 需要展示的信息
    val building1_info = VisLabel("")
    // 展示信息的媒介
    val building1_info_window = VisWindow("").apply {
        titleLabel.text("B1 信息", language, 24)
        setPosition(80f, 80f)
        setSize(window_width - 160f, window_height - 160f)
        add(building1_info)
        closeOnEscape()
        addCloseButton()
    }/*
    val building1_info_button = VisTextButton("").apply {
        alpha = 0.5f
        setPosition(128f, 128f)
        setSize(64f, 64f)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                control_stage.addActor(building1_info_window)
            }
        })
    }*/
    val input_adapter = object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val world_position = world_viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
            if (world_position.x - building1.position.x <= building1.sprite.width) {
                control_stage.addActor(building1_info_window)
            }
            return true
        }
    }
    override fun show() {
        // world_stage.addActor(building1_info_button)
        Gdx.input.inputProcessor = multiplexer
        multiplexer.addProcessor(control_stage)
        multiplexer.addProcessor(world_stage)
        multiplexer.addProcessor(input_adapter)
    }
    override fun update(tick: Float) {
        factory_helper.update(listOf(building1), tick)
    }
    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.7f, 1.0f)
        /* if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            building1_info_window.remove()
        }*/
        building1_info.text(
            "工厂进度：${building1.progress} / ${building1.recipe.time}" +
            "\n工厂存储：${building1.item_store_list}",
            language, 24
        )
        world_camera.update()
        world_viewport.apply()
        batch_2d.projectionMatrix = world_camera.combined
        batch_2d.begin()
        building1.sprite.draw(batch_2d)
        batch_2d.end()
        control_camera.update()
        control_viewport.apply()
        world_stage.act(delta)
        world_stage.draw()
        control_stage.act(delta)
        control_stage.draw()
    }
}