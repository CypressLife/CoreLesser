package com.github.corelesser.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.corelesser.app.assets.FontCreate
import com.github.corelesser.app.assets.FontCreate.text
import com.github.corelesser.app.lang.Language
import com.github.corelesser.core.Radius
import com.github.corelesser.core.Vector2D
import com.github.corelesser.core.entity.Buildings
import com.github.corelesser.core.entity.TestCarrier
import com.github.corelesser.core.entity.TestCarrierCenter
import com.github.corelesser.core.entity.TestFactory
import com.github.corelesser.core.entity.ables.buildings.CarrierCenterHelper
import com.github.corelesser.core.entity.ables.buildings.FactoryController
import com.github.corelesser.core.entity.ables.buildings.P2Pchannel
import com.github.corelesser.core.entity.ables.buildings.Recipe
import com.github.corelesser.core.entity.ables.buildings.Storeable
import com.github.corelesser.core.entity.ables.units.CarrierController
import com.github.corelesser.core.entity.ables.units.CarrierHelper
import com.github.corelesser.core.entity.ables.units.UnitEntityMove
import com.github.corelesser.core.materials.Iron
import com.github.corelesser.core.materials.Steel
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.alpha
import ktx.app.KtxApplicationAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.async.KtxAsync
import ktx.scene2d.actors
import kotlin.math.PI

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
    fun update(tick: Float) {
        screen?.update(tick)
    }

    // 渲染更新
    override fun render() {
        // 固定时间步长 40Hz
        times += delta_time
        while (times > 0.025f) {
            times -= 0.025f
            update(delta_time)
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
    abstract fun update(tick: Float)
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
                // screen = GameScreen(game)
                screen = UITestScreen(game)
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
    override fun update(tick: Float) {}
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
    val world_camera = OrthographicCamera().apply {
        zoom = 2f
    }
    val world_viewport = ExtendViewport(1280f, 720f, world_camera)
    val control_camera = OrthographicCamera()
    val control_viewport = ScreenViewport(control_camera)

    // 屏幕控件
    val info_text = VisLabel("").apply {
        setPosition(20f, 320f)
    }
    val building_3_info_button = VisTextButton("").apply {
        setPosition(96f, 512f)
        setSize(96f, 96f)
        addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                control_stage.addActor(building_3_info_window)
                Gdx.input.inputProcessor = control_stage
            }
        })
    }
    val building_3_info = VisLabel("")
    val building_3_info_window = VisWindow("").apply {
        setSize(256f, 128f)
        addActor(building_3_info)
    }
    val world_stage = Stage(world_viewport).apply {
        addActor(building_3_info_button)
    }
    val control_stage = Stage(control_viewport).apply {
        addActor(info_text)
    }
    // 屏幕布局

    // 测试代码
    val building1 = TestFactory(
        0L,
        Vector2D.pair(48f, 48f),
        Radius.create(0f),
        item_capacity = 100L,
        recipe = Recipe(1f, mapOf(), mapOf(Iron to 2L)),
        texture = Texture("Factory.png"),
    )
    val building2 = Buildings.create_store(
        1L,
        Vector2D.pair(640f, 480f),
        Radius.create(0f),
        item_capacity = 50L,
        texture = Texture("Factory.png"),
    ).apply {
        item_store_list[Iron] = 3
    }
    val building3 = Buildings.create_store(
        6L,
        Vector2D.pair(96f, 512f),
        Radius.create(0f),
        item_capacity = 50L,
        texture = Texture("Factory.png"),
    )
    val test_factory = TestFactory(
        4L,
        Vector2D.pair(690f, 360f),
        Radius.create(0f),
        item_capacity = 10L,
        recipe = Recipe(0.5f, mapOf(Iron to 1L), mapOf(Steel to 1L)),
        texture = Texture("Factory.png"),
    ).apply {
        item_store_list[Iron] = 10L
    }
    val test_channel_1 = P2Pchannel(building1 as Storeable, test_factory, Iron , false)
    val test_channel_2 = P2Pchannel(building1, building3, Steel, true)
    val test_carrier_1 = TestCarrier(
        2L,
        Vector2D.pair(690f, 360f),
        Radius.create(0f),
        acceleration = 1f,
        deceleration = 2.5f,
        rotate = Radius.create(PI.toFloat() / 16f),
        speed_max = 10f,
        target = building1.position,
        item_capacity = 1L,
        channel = test_channel_1,
        texture = Texture("TestRunner.png"),
    )
    val test_carrier_2 = TestCarrier(
        3L,
        Vector2D.pair(20f, 600f),
        Radius.create(0f),
        acceleration = 1f,
        deceleration = 2.5f,
        rotate = Radius.create(PI.toFloat() / 16f),
        speed_max = 10f,
        target = building1.position,
        item_capacity = 1L,
        channel = test_channel_1,
        texture = Texture("TestRunner.png"),
    )
    val test_carrier_3 = TestCarrier(
        7L,
        Vector2D.pair(0f, 0f),
        Radius.create(0f),
        acceleration = 2f,
        deceleration = 5f,
        rotate = Radius.create(PI.toFloat() / 4f),
        speed_max = 30f,
        target = test_factory.position,
        item_capacity = 10L,
        channel = test_channel_2,
        texture = Texture("TestRunner.png"),
    ).apply {
        sprite.scale(2f)
    }
    val test_center = TestCarrierCenter(
        id = 5L,
        Vector2D.pair(690f, 360f),
        Radius.create(0f),
    ).apply {
        carriers[2L] = test_carrier_1
        carriers[3L] = test_carrier_2
        carriers[7L] = test_carrier_3
        channels.add(test_channel_1)
        channels.add(test_channel_2)
    }
    val carrier_helper = CarrierHelper()
    val carrier_controller = CarrierController()
    val move_helper = UnitEntityMove()
    val factory_helper = FactoryController()
    val center_helper = CarrierCenterHelper()

    override fun update(tick: Float) {
        factory_helper.update(listOf(building1, test_factory), tick)
        center_helper.update(
            listOf(test_center),
            carrier_controller,
            carrier_helper,
            move_helper,
            tick
        )
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.postRunnable {
                building_3_info_window.remove()
                Gdx.input.inputProcessor = world_stage
            }
        }
        clearScreen(0.2f, 0.2f, 0.2f, 1f)
        // 游戏画面渲染
        world_camera.update()
        world_viewport.apply()
        batch_2d.projectionMatrix = world_camera.combined
        batch_2d.begin()
        // building1.sprite.draw(batch_2d)
        building2.sprite.draw(batch_2d)
        test_carrier_1.sprite.draw(batch_2d)
        test_carrier_2.sprite.draw(batch_2d)
        test_carrier_3.sprite.draw(batch_2d)
        batch_2d.end()
        // 游戏 UI 渲染
        control_camera.update()
        control_viewport.update(window_width, window_height, true)
        control_viewport.apply()
        info_text.text("B1仓储：${building1.item_store_list}" +
                "\n无人机3背包：${test_carrier_3.now_item_type} 数量：${test_carrier_3.now_item_value} / ${test_carrier_3.item_capacity}" +
                "\n工厂仓储：${test_factory.item_store_list}" +
                "\nB3仓库仓储：${building3.item_store_list}", language, 24)
        world_stage.act(delta)
        world_stage.draw()
        control_stage.act(delta)
        control_stage.draw()
    }
    override fun show() {
        Gdx.input.inputProcessor = world_stage
        test_carrier_1.target = building1.position
    }
    override fun resize(width: Int, height: Int) {
        world_viewport.update(width, height, true)
        control_viewport.update(width, height, true)
    }
    override fun dispose() {
        control_stage.dispose()
    }
}