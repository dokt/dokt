/** 2D coordinate system */
package app.dokt.common

import app.dokt.common.support.Point2D

//#region Dimension
expect class Dimension() {
    var width: Int
    var height: Int
    constructor(width: Int, height: Int)
}
val Dimension.area get() = width * height
val Dimension.point get() = Point(width, height)

/** Aspect ratio */
val Dimension.ratio get() = width.toDouble() / height

val Dimension.text get() = "$width x $height"

operator fun Dimension.contains(dimension: Dimension) = dimension.width <= width && dimension.height <= height

// TODO investigate equal cases
operator fun Dimension.contains(point: Point) = point.x <= width && point.y <= height

/* TODO no point
fun Dimension.scale(max: Int): Dimension {
    if (width <= max && height <= max) return this
    val ratio = max.toDouble() / width.coerceAtLeast(height)
    return Dimension((width * ratio).roundToInt(), (height * ratio).roundToInt())
}*/
val dimensionless = Dimension()
val pixel = Dimension(1, 1)

val fullscreen = 4.0 / 3
val widescreen = 16.0 / 9

//#region Display resolutions
//https://en.wikipedia.org/wiki/720p#/media/File:Vector_Video_Standards8.svg
val CGA = Dimension(320, 200)
val VGA = Dimension(640, 480)
val StandardHD = Dimension(1280, 720)
val FullHD = Dimension(1920, 1080)
val UHD = Dimension(3840, 2160)
//#endregion
//#endregion

// TODO
expect class Line() {
    fun getX1(): Double
    fun getY1(): Double
    fun getX2(): Double
    fun getY2(): Double
    //fun constructor(x1: Double, y1: Double, x2: Double, y2: Double)
}

//#region Rectangle
// TODO inspect Rectangle 2D
expect class Rectangle() {
    var x: Int
    var y: Int
    var width: Int
    var height: Int
    constructor(x: Int, y: Int, width: Int, height: Int)
    constructor(width: Int, height: Int)
    constructor(dimension: Dimension)
    constructor(point: Point)
    constructor(point: Point, dimension: Dimension)
    operator fun contains(point: Point): Boolean
    operator fun contains(rectangle: Rectangle): Boolean
    fun getLocation(): Point
    fun getSize(): Dimension
    fun intersects(rectangle: Rectangle): Boolean
    fun intersection(rectangle: Rectangle): Rectangle
    fun union(rectangle: Rectangle): Rectangle
    fun isEmpty(): Boolean
    fun outcode(x: Double, y: Double): Int
    fun intersectsLine(x1: Double, y1: Double, x2: Double, y2: Double): Boolean // TODO line
    fun getMaxX(): Double
    fun getMaxY(): Double
    fun getCenterX(): Double
    fun getCenterY(): Double
}
val Rectangle.area get() = width * height
val Rectangle.text get() = "($x, $y; $width x $height)"
operator fun Rectangle.minus(point: Point) = Rectangle(x - point.x, y - point.y, width, height)
operator fun Rectangle.plus(point: Point) = Rectangle(x + point.x, y + point.y, width, height)
//#endregion

//#region Point
/** A point representing a location in (x, y) coordinate space, specified in integer precision. */
expect class Point() : Point2D {
    var x: Int
    var y: Int
    constructor(x: Int, y: Int)
}
val Point.text get() = "($x, $y)"

/** The zero point in coordinate system. */
val origin = Point()

val Point.dimension get() = Dimension(x, y)
operator fun Point.plus(dimension: Dimension) = Rectangle(this, dimension)
//#endregion
