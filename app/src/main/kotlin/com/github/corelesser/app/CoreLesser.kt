package com.github.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.corelesser.app.assets.FontCreate
import com.github.corelesser.app.assets.FontCreate.text
import com.github.corelesser.app.lang.Language
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.Buildings
import com.github.corelesser.core.entity.Carrier
import com.github.corelesser.core.entity.EntityFactory
import com.github.corelesser.core.entity.ables.StoreHelper
import com.github.corelesser.core.entity.ables.Storeable
import com.github.corelesser.core.entity.manager.EntityManager
import com.github.corelesser.core.materials.Iron
import com.github.corelesser.core.materials.Item
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.app.KtxApplicationAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync
import org.jetbrains.kotlin.gradle.internal.util.UnitStats
import kotlin.collections.set
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

// 游戏入口
class CoreLesser : KtxApplicationAdapter {
    // 累加时间
    private var times = 0f

    // 游戏语言
    var language = Language.Simple_Chinese

    // 当前屏幕
    var screen : CoreLesserScreen? = null
        set(value) {
            value?.show()
            field?.dispose()
            field = value
        }

    // 获取屏幕长宽
    val window_width: Int
        get() = Gdx.graphics.width
    val window_height: Int
        get() = Gdx.graphics.height

    // 获取帧时间
    val delta_time: Float
        get() = Gdx.graphics.deltaTime
    val batch_2d: SpriteBatch by lazy { SpriteBatch() }

    // 入口类初始化
    override fun create() {
        VisUI.load()
        FontCreate.initialized()
        KtxAsync.initiate()
        screen = MainScreen(this)
    }

    override fun resize(width: Int, height: Int) {
        screen?.resize(width, height)
    }

    // 逻辑更新
    fun update() {
        screen?.update()
    }

    // 渲染更新
    override fun render() {
        // 固定时间步长 40Hz
        times += delta_time
        while (times > 0.025f) {
            times -= 0.025f
            update()
        }
        screen?.render(delta_time)
    }

    // 内存释放
    override fun dispose() {
        screen?.dispose()
        VisUI.dispose()
        batch_2d.dispose()
    }
}
// 带入口类成员参数的屏幕类
abstract class CoreLesserScreen(private val game: CoreLesser): KtxScreen {
    val language
        get() = game.language
    var screen
        get() = game.screen
        set(value) {
            game.screen = value
        }
    val batch_2d
        get() = game.batch_2d
    val window_width
        get() = game.window_width
    val window_height
        get() = game.window_height
    abstract fun update()
}
// 游戏菜单屏幕
class MainScreen(private val game: CoreLesser): CoreLesserScreen(game) {
    // 屏幕控件
    val version_info = VisLabel("失核者\nCoreLesser\nVersion: 0.1\nbuild-1.0").text(
        "失核者\nCoreLesser\nVersion: 0.1\nbuild-1.0", Language.Simple_Chinese, 16
    )
    val battle_button = VisTextButton("").apply {
        text(language.text.battle, language, 24)
        addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                screen = GameScreen(game)
            }
        })
    }
    val option_button = VisTextButton(language.text.option).apply {
        text(language.text.option, language, 24)
        addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                option_window.isVisible = true
            }
        })
    }
        val option_window_back_button = VisTextButton("Back").apply {
            addListener(object: ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    option_window.isVisible = false
                }
            })
        }
        val option_window: VisWindow = VisWindow("Options").apply {
            isVisible = false
            width = window_width.toFloat()
            height = window_height.toFloat()
            color = Colors.get("BLACK")
            add(option_window_back_button)
        }
    // 屏幕布局
    val info_table = VisTable().apply {
        add(version_info)
        setPosition(width / 8f, height / 8f)
    }
    val button_table = VisTable().apply {
        add(battle_button).width(120f).height(80f).row()
        add(option_button).width(120f).height(80f).row()
        setPosition((window_width.toFloat() - width) / 2f, (window_height.toFloat() - height) / 2f)
    }
    // 屏幕基本内容
    val camera = OrthographicCamera()
    val viewport = ScreenViewport(camera)
    val stage : Stage by lazy{
        Stage(viewport).apply {
            addActor(info_table)
            addActor(button_table)
            addActor(option_window)
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }
    override fun resize(width: Int, height: Int) {
        info_table.pack()
        info_table.setPosition(info_table.width / 8f, info_table.height / 8f)
        button_table.pack()
        button_table.setPosition((window_width - button_table.width) / 2f, (window_height - button_table.height) / 2f)
    }
    override fun update() {}
    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f, 1.0f)
        camera.update()
        viewport.apply()
        stage.act(delta)
        stage.draw()
    }
    override fun dispose() {
        stage.dispose()
    }
}
// 游戏界面屏幕
class GameScreen(private val game: CoreLesser) : CoreLesserScreen(game) {
    // 相机和视口
    val world_camera = OrthographicCamera()
    val world_viewport = ExtendViewport(1280f, 720f, world_camera)
    val control_camera = OrthographicCamera()
    val control_viewport = ScreenViewport(control_camera)

    // 屏幕控件
    val stage = Stage(control_viewport)
    // 屏幕布局

    // 测试代码
    val building1 = Buildings.create_store(
        0L,
        Vector2D.pair(48f, 48f),
        Radius.create(0f),
        item_capacity = 100L,
        texture = Texture("Factory.png"),
    ).apply { item_store_list[Iron] = 100L }
    val building2 = Buildings.create_store(
        1L,
        Vector2D.pair(960f, 480f),
        Radius.create(0f),
        item_capacity = 100L,
        texture = Texture("Factory.png"),
    )
    val carrier = Carrier(
        2L,
        Vector2D.pair(690f, 360f),
        Radius.create(0f),
        acceleration = 1f,
        deceleration = 2.5f,
        speed = 0f,
        speed_max = 10f,
        target = Vector2D.pair(48f, 48f),
        item_capacity = 10L,
        texture = Texture("TestRunner.png")
    )
    val direction
        get() = carrier.target - carrier.position
    val rotate
        get() = atan2(direction.y, direction.x)
    val distance
        get() = (carrier.target - carrier.position).length()
    val entity_manager = EntityManager().apply {
        add_entity(building1)
        add_entity(building2)
        add_entity(carrier)
    }
    val store_helper = StoreHelper(entity_manager)

    override fun update() {
        building2.item_store_list[Iron]?.let {
            if (it == building2.item_capacity)
                return
        }
        if (carrier.arrive && carrier.target == building1.position) {
            building1.item_store_list[Iron] = store_helper.accept_item(carrier, Iron, 20L)
            carrier.target (building2.position)
        } else if (carrier.arrive && carrier.target == building2.position) {
            carrier.item_store_list[Iron] = store_helper.accept_item(building2, Iron, 20L)
            carrier.target (building1.position)
        }
        carrier.move()
    }

    override fun render(delta: Float) {
        clearScreen(0.2f, 0.2f, 0.2f, 1f)
        // 游戏画面渲染
        world_camera.update()
        world_viewport.update(game.window_width, game.window_height, true)
        world_viewport.apply()
        batch_2d.projectionMatrix = world_camera.combined
        batch_2d.begin()
        building1.sprite.draw(batch_2d)
        building2.sprite.draw(batch_2d)
        carrier.sprite.draw(batch_2d)
        batch_2d.end()
        // 游戏 UI 渲染
        control_camera.update()
        control_viewport.update(window_width, window_height, true)
        control_viewport.apply()
    }
    override fun show() {
        Gdx.input.inputProcessor = stage
        carrier.target(building1.position)
    }
    override fun resize(width: Int, height: Int) {
        world_viewport.update(width, height, true)
        control_viewport.update(width, height, true)
    }
    override fun dispose() {
        stage.dispose()
    }
}