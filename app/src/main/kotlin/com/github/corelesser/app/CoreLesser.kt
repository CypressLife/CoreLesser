package com.github.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisImageTextButton
import com.kotcrab.vis.ui.widget.VisTable
import ktx.app.KtxApplicationAdapter
import ktx.app.clearScreen
import ktx.async.KtxAsync

// 游戏入口
class CoreLesser : KtxApplicationAdapter {
    // 累加时间
    private var times = 0f
    private var step_time = 0f
    var irons: Long = 0

    // 获取屏幕长宽
    val window_width: Int
        get() = Gdx.graphics.width
    val window_height: Int
        get() = Gdx.graphics.height

    // 获取帧时间
    val delta_time: Float
        get() = Gdx.graphics.deltaTime
    val world_camera: OrthographicCamera by lazy { OrthographicCamera() }
    val world_viewport: ExtendViewport by lazy { ExtendViewport(1280f, 720f, world_camera) }
    val batch_2d: SpriteBatch by lazy { SpriteBatch() }
    val control_camera: OrthographicCamera by lazy { OrthographicCamera() }
    val control_viewport: ScreenViewport by lazy { ScreenViewport() }

    // 按钮列表
    val irons_label: VisImageTextButton by lazy {
        VisImageTextButton(
            "Irons:$irons",
            TextureRegionDrawable(Texture("iron.png"))
        )
    }
    val control_stage: Stage by lazy {
        Stage(control_viewport).apply {
            isDebugAll = true
            val root_table = VisTable().apply {
                setFillParent(true)
                top().left()
                irons_label.apply { setSize(512f, 512f) }
                add(irons_label)
            }
            addActor(root_table)
        }
    }
    val building_sprite1: Sprite by lazy {
        Sprite(Texture("Factory.png")).apply {
            setPosition(128f, 128f)
            setCenter(128f - width / 2, 128f - height / 2)
        }
    }
    val building_sprite2: Sprite by lazy {
        Sprite(Texture("Factory.png")).apply {
            setPosition(512f, 128f)
            setCenter(512f - width / 2, 128f - height / 2)
        }
    }

    override fun create() {
        VisUI.load()
        KtxAsync.initiate()
        world_camera.update()
        control_camera.update()
    }

    // 逻辑更新
    fun update() {
        step_time += delta_time
        if (step_time > 1f) {
            irons += 1
            step_time -= 1f
        }
    }

    // 渲染更新
    override fun render() {
        // 固定时间步长 40Hz
        times += delta_time
        if (times > 0.025f) {
            do {
                times -= 0.025f
                update()
            } while (times < 0f)
        }
        irons_label.text = "irons:$irons"
        clearScreen(0.2f, 0.2f, 0.2f, 1f)
        // 游戏画面渲染
        world_camera.update()
        world_viewport.update(window_width, window_height, true)
        world_viewport.apply()
        batch_2d.projectionMatrix = world_camera.combined
        batch_2d.begin()
        building_sprite1.draw(batch_2d)
        building_sprite2.draw(batch_2d)
        batch_2d.end()
        // 游戏 UI 渲染
        control_camera.update()
        control_viewport.update(window_width, window_height, true)
        control_viewport.apply()
        control_stage.act()
        control_stage.draw()
    }

    override fun dispose() {
        VisUI.dispose()
        control_stage.dispose()
        batch_2d.dispose()
    }
}