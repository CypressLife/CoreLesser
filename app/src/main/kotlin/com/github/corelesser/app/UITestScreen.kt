package com.github.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.corelesser.app.assets.FontCreate.text
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.BuildingEntity
import com.github.corelesser.core.entity.TestFactory
import com.github.corelesser.core.entity.TestStore
import com.github.corelesser.core.entity.ables.SpriteDrawable
import com.github.corelesser.core.entity.ables.buildings.Factoriable
import com.github.corelesser.core.entity.ables.buildings.FactoryController
import com.github.corelesser.core.entity.ables.buildings.Recipe
import com.github.corelesser.core.entity.ables.buildings.Storeable
import com.github.corelesser.core.materials.Gold
import com.github.corelesser.core.materials.Iron
import com.github.corelesser.core.materials.Item
import com.github.corelesser.core.materials.Steel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.alpha
import ktx.app.clearScreen

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
    val building2 = TestStore(
        2L,
        Vector2D.pair(128f, 256f),
        Radius.create(0f),
        item_capacity = 20L,
        texture = Texture("Factory.png")
    ).apply {
        item_store_list[Iron] = 20L
        item_store_list[Steel] = 10L
    }
    val buildings = listOf<BuildingEntity>(building1, building2)
    val factory_helper = FactoryController()
    // 一个表示世界的舞台
    val world_stage = Stage(world_viewport)
    // 一个展示信息的舞台
    val control_stage = Stage(control_viewport)
    // 需要展示的信息
    val building_info = VisLabel("")
    // 展示信息的媒介
    val building_info_window = object : VisWindow(""){
        override fun close() {
            now_open_building = null
            super.close()
        }
    }.apply {
        titleLabel.text("建筑信息", language, 24)
        setPosition(80f, 80f)
        setSize(window_width - 160f, window_height - 160f)
        add(building_info)
        closeOnEscape()
        addCloseButton()
    }
    private var now_open_building: BuildingEntity? = null
    private fun touch_hit(position: Vector2): BuildingEntity? {
        for (building in buildings) {
            if (building !is SpriteDrawable) return null
            if (building.sprite.boundingRectangle.contains(position)) {
                return building
            }
        }
        return null
    }
    val input_adapter = object : InputAdapter() {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val hit = touch_hit(world_viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat())))
            now_open_building = hit
            if (hit != null) {
                building_info_window.alpha = 1f
                control_stage.addActor(building_info_window)
            } else {
                building_info_window.fadeOut()
                now_open_building = null
            }
            return true
        }
    }
    override fun show() {
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
        val info_text = when(val building = now_open_building) {
            is Factoriable -> "${building.display_name}" +
                    "\n${building.progress} / ${building.recipe.time}" +
                    "\n${building.item_list_print()}"
            is Storeable -> "${building.display_name}" +
                    "\n${building.item_list_print()}"
            null -> ""
            else -> "${building.id}"
        }
        building_info.text(info_text, language, 24)
        world_camera.update()
        world_viewport.apply()
        batch_2d.projectionMatrix = world_camera.combined
        batch_2d.begin()
        building1.sprite.draw(batch_2d)
        building2.sprite.draw(batch_2d)
        batch_2d.end()
        control_camera.update()
        control_viewport.apply()
        world_stage.act(delta)
        world_stage.draw()
        control_stage.act(delta)
        control_stage.draw()
    }
}