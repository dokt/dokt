/** 2D coordinate system */
package app.dokt.common

import kotlin.math.roundToInt

interface Pointed {
    val x: Int
    val y: Int

    fun toDimension() = Dim(x, y)
}

/** Point (avoiding conflict with java.awt.Point) */
expect class Pt(x: Int = 0, y: Int = 0) : Pointed {
    operator fun plus(dim: Dim): Rect

    operator fun plus(rect: Rect): Rect

    companion object {
        val ZERO: Pt
    }
}

interface Dimensioned {
    val width: Int
    val height: Int
    val area get() = width * height
    val dot get() = width == 1 && height == 1

    fun scale(max: Int): Dim {
        if (width <= max && height <= max) return this as? Dim ?: Dim(width, height)
        val ratio = max.toDouble() / width.coerceAtLeast(height)
        return Dim((width * ratio).roundToInt(), (height * ratio).roundToInt())
    }

    fun toPoint() = Pt(width, height)
}

/** Dimension (avoiding conflict with java.awt.Dimension) */
expect class Dim(width: Int = 0, height: Int = 0) : Comparable<Dim>, Dimensioned {
    companion object {
        val ONE: Dim
        val ZERO: Dim
    }
}

val HD = Dim(1920, 1080)
val UHD = Dim(3840, 2160)

/** Rectangle (avoiding conflict with java.awt.Rectangle) */
expect class Rect : Dimensioned, Pointed {
    val location: Pt
    val size: Dim

    constructor(x: Int, y: Int, width: Int = 0, height: Int = 0)

    constructor(x: Int, y: Int, dimension: Dim)

    constructor(point: Pt, width: Int = 0, height: Int = 0)

    constructor(point: Pt, dimension: Dim)

    constructor(point: Pt, rectangle: Rect)

    operator fun minus(point: Pt): Rect

    operator fun plus(point: Pt): Rect

    companion object {
        val DOT: Rect
        val ZERO: Rect
    }
}
