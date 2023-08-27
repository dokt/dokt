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

operator fun Dimension.times(multiplier: Int) = Dimension(width * multiplier, height * multiplier)

/* TODO no point
fun Dimension.scale(max: Int): Dimension {
    if (width <= max && height <= max) return this
    val ratio = max.toDouble() / width.coerceAtLeast(height)
    return Dimension((width * ratio).roundToInt(), (height * ratio).roundToInt())
}*/
val dimensionless = Dimension()
val pixel = Dimension(1, 1)

const val FULLSCREEN = 4.0 / 3
const val WIDESCREEN = 16.0 / 9

//#region Display resolutions
//https://en.wikipedia.org/wiki/720p#/media/File:Vector_Video_Standards8.svg
private const val CGA_WIDTH = 320
private const val CGA_HEIGHT = 200
private const val VGA_HEIGHT = 480
private const val STANDARD_HD_HEIGHT = 720
private const val FULL_HD_HEIGHT = 1080

/** CGA 320 x 200 */
val CGA = Dimension(CGA_WIDTH, CGA_HEIGHT)

/** VGA 640 x 480 */
val VGA = Dimension(CGA.width * 2, VGA_HEIGHT)

/** Standard HD 720p 1280 x 720 */
val StandardHD = Dimension(VGA.width * 2, STANDARD_HD_HEIGHT)

/** Full HD 1920 x 1080 */
val FullHD = Dimension(StandardHD.width + VGA.width, FULL_HD_HEIGHT)

/** 4K UHD 3840 x 2160 */
val UHD = FullHD * 2
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
    constructor(size: Dimension)
    constructor(point: Point)
    constructor(point: Point, size: Dimension)
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
fun Rectangle(x: Int, y: Int, size: Dimension) = Rectangle(x, y, size.width, size.height)
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
