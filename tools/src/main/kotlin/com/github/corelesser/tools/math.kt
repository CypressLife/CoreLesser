package com.github.corelesser.tools

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.pow
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.sqrt

private const val PI1 = PI.toFloat()
private const val PI2 = (2 * PI).toFloat()

// 正三角函数
fun sin(radius: Radius): Float = sin(radius.value)
fun cos(radius: Radius): Float = cos(radius.value)
fun tan(radius: Radius): Float = tan(radius.value)

// 弧度
@JvmInline
value class Radius private constructor(val value: Float) {
    // 构造函数
    companion object {
        fun create(value: Float): Radius = Radius(((value + PI1) % PI2 + PI2) % PI2 - PI1)
    }
    // 运算
    operator fun plus(other: Radius) = create(this.value + other.value)
    operator fun minus(other: Radius) = create(this.value - other.value)
    operator fun times(other: Float) = create(this.value * other)
    operator fun div(other: Float) = create(this.value / other)
    operator fun rem(other: Float) = create(this.value % other)
    // 比较
    operator fun compareTo(other: Radius) = this.value.compareTo(other.value)
    // 打印
    override fun toString(): String = "${this.value}"
}

// 平面向量, 通过一个 Long 存储两个 Float 属性
@JvmInline
value class Vector2D(private val pack: Long) {
    /*
     * 构造函数: 用两个 Float 打包, Long 的高 32 位存储 x 分量, 低 32 位存储 y 分量
     * x 分量在转换并平移后低 32 位全部为 0 而 y 分量在转换后高 32 位可能全部未 1 造成 or 合并后丢失 x 分量, 通过 and 和掩码清空高 32 位
     * 主构造函数为标准数学表述法, 即通过坐标数对描述向量本身的方向与大小, 使用 private 修饰防止语义歧义
     * 次级构造函数通过伴生对象公开并返回一个自身, 所有次级构造函数均使用 public 修饰
     */
    private constructor(x: Float, y: Float): this(
        (x.toRawBits().toLong() shl 32) or (y.toRawBits().toLong() and 0xFFFFFFFFL)
    )
    /*
     * 数对创建
     * 性质创建
     * 投影创建
     */
    companion object {
        fun pair(x: Float, y: Float): Vector2D = Vector2D(x, y)
        fun mole(length: Float, radius: Radius): Vector2D = Vector2D(length * cos(radius), length * sin(radius))
        fun shadow(x: Float, radius: Radius): Vector2D = Vector2D(x, x * tan(radius))
    }

    // 两个分量
    val x: Float get() = Float.fromBits((pack shr 32).toInt())
    val y: Float get() = Float.fromBits(pack.toInt())

    // 加法操作符
    operator fun plus(other: Vector2D): Vector2D = Vector2D(this.x + other.x, this.y + other.y)

    // 减法操作符
    operator fun minus(other: Vector2D): Vector2D = Vector2D(this.x - other.x, this.y - other.y)

    // 倍率操作符
    operator fun times(scale: Float): Vector2D = Vector2D(this.x * scale, this.y * scale)

    // 副本
    fun copy(): Vector2D = Vector2D(this.x, this.y)

    // 点积
    fun dot(other: Vector2D): Float = (this.x * other.x + this.y * other.y)

    // 面积
    fun lengthArea(): Float = (x.pow(2) + y.pow(2))

    // 模长
    fun length(): Float = sqrt(lengthArea())

    // 方向
    fun direction(direction: Radius): Vector2D = Vector2D(length() * sin(direction), length() * cos(direction))

    // 打印
    override fun toString(): String = "(x: $x, y: $y)"
}

// 平面索引, 通过一个 Long 存储两个 Int 属性
@JvmInline
value class Location(private val pack: Long) {
    // 唯一构造函数, 生成一个确定的二维索引
    constructor(x: Int, y: Int): this(
        (x.toLong() shl 32) or (y.toLong() and 0xFFFFFFFFL)
    )

    // 两个分量
    val x get() = (pack shr 32).toInt()
    val y get() = pack.toInt()

    // 方向
    fun direction(): Float = (atan2(x.toDouble(), y.toDouble()).toFloat())

    // 打印
    override fun toString(): String = "(x: $x, y: $y)"
}