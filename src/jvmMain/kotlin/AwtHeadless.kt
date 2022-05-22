// https://stackoverflow.com/questions/41067235/what-is-the-benefit-of-setting-java-awt-headless-true
// https://www.oracle.com/technical-resources/articles/javase/headless.html
package app.dokt

import kotlinx.serialization.Serializable
import java.awt.*
import java.awt.geom.*
import java.awt.image.*
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.div
import kotlin.math.roundToInt

//#region Draw
val TRANSPARENT = Color(0, true)

operator fun Font.times(multiplier: Double) = Font(name, style, (size * multiplier).roundToInt())

operator fun Font.times(multiplier: Int) = Font(name, style, size * multiplier)
//#endregion

//#region Image
val DOT = binaryImage()
const val PNG = "png"

fun binaryImage(width: Int = 1, height: Int = 1) = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)

fun equal(a: BufferedImage?, b: BufferedImage?) = a?.isEqual(b) ?: (b == null)

val BufferedImage.bytes get() = 4L * width * height

val BufferedImage.lastPixelColor get() = Color(getRGB(width - 1, height - 1))

val BufferedImage.size get() = Dimension(width, height)

fun BufferedImage.isEqual(other: BufferedImage?) = other != null
        && type == other.type
        && width == other.width
        && height == other.height
        && raster.isEqual(other.raster)

val File.image get() = ImageIO.read(this)!!

val Path.image get() = toFile().image

fun RenderedImage.write(file : File) {
    when (file.extension) {
        PNG -> writePng(file)
        else -> throw UnsupportedOperationException(file.extension)
    }
}

fun RenderedImage.write(path : Path, filename: String) = write((path / filename).toFile())

fun RenderedImage.writePng(file : File) = ImageIO.write(this, PNG, file)

fun RenderedImage.writePng(path : Path, filename: String) =
    (path / "$filename.$PNG").toFile().apply { writePng(this) }

//#region Image data
fun DataBuffer.isEqual(other: DataBuffer): Boolean {
    if (dataType != other.dataType || numBanks != other.numBanks) return false
    if (other is DataBufferInt) {
        return if (this is DataBufferInt) isEqual(other) else false
    }
    if (other is DataBufferByte) {
        return if (this is DataBufferByte) isEqual(other) else false
    }
    throw NotImplementedError("isEqual not implemented for class $javaClass!")
}

fun DataBufferByte.isEqual(other: DataBufferByte): Boolean {
    for (bank in 0 until numBanks) {
        val data = getData(bank)
        val otherData = other.getData(bank)
        if (!Arrays.equals(data, otherData)) return false
    }
    return true
}

/** https://stackoverflow.com/questions/11006394/is-there-a-simple-way-to-compare-bufferedimage-instances */
fun DataBufferInt.isEqual(other: DataBufferInt): Boolean {
    for (bank in 0 until numBanks) {
        val data = getData(bank)
        val otherData = other.getData(bank)
        if (!Arrays.equals(data, otherData)) return false
    }
    return true
}

fun Raster.isEqual(other: Raster) = dataBuffer.isEqual(other.dataBuffer)

val Raster.lastPixel get() = getPixel(width - 1, height - 1, IntArray(numBands))
//#endregion
//#endregion

//#region Shape
@Serializable
data class Dim(private val width: Int = 0, val height: Int = 0) : Dimension(width, height), Comparable<Dimension> {
    val h get() = height
    val w get() = width

    override operator fun compareTo(other: Dimension) = width.compareTo(other.width) + height.compareTo(other.height)

    //#region Disabled setters
    override fun setSize(width: Double, height: Double) = throw IllegalStateException()
    override fun setSize(d: Dimension?) = throw IllegalStateException()
    override fun setSize(width: Int, height: Int) = throw IllegalStateException()
    override fun setSize(d: Dimension2D?) = throw IllegalStateException()
    //#endregion

    companion object {
        val ONE = Dim(1, 1)
    }
}

val Dimension.dim get() = Dim(width, height)
val Dimension.dot get() = width == 1 && height == 1
val Dimension.resolution get() = "$width x $height"
fun Dimension.scale(max: Int): Dimension {
    if (width <= max && height <= max) return this
    val ratio = max.toDouble() / width.coerceAtLeast(height)
    return Dimension((width * ratio).roundToInt(), (height * ratio).roundToInt())
}
val HD = Dim(1920, 1080)
val UHD = Dim(3840, 2160)

@Serializable
data class Pt(val x: Int = 0, val y: Int = 0) : Point(x, y) {
    //#region Disabled setters
    override fun setLocation(p: Point?) = throw IllegalStateException()
    override fun setLocation(x: Int, y: Int) = throw IllegalStateException()
    override fun setLocation(x: kotlin.Double, y: kotlin.Double) = throw IllegalStateException()
    override fun setLocation(p: Point2D?) = throw IllegalStateException()
    override fun move(x: Int, y: Int) = throw IllegalStateException()
    override fun translate(dx: Int, dy: Int) = throw IllegalStateException()
    //#endregion
    companion object {
        val ZERO = Pt()
    }

    operator fun plus(dim: Dim) = Rect(this, dim)

    operator fun plus(rect: Rect) = Rect(this, rect)
}

val Point.pt get() = Pt(x, y)
val Point.text get() = "$x;$y"

@Serializable
data class Rect(val x: Int = 0, val y: Int = 0, val width: Int = 0, val height: Int = 0) : Rectangle(x, y, width, height) {
    constructor(point: Point, width: Int = 0, height: Int = 0) : this (point.x, point.y, width, height)

    constructor(point: Point, dimension: Dimension) : this (point.x, point.y, dimension.width, dimension.height)

    constructor(x: Int, y: Int, dimension: Dimension) : this (x, y, dimension.width, dimension.height)

    constructor(point: Point, rectangle: Rectangle) : this (point.x + rectangle.x, point.y + rectangle.y, rectangle.size)

    //#region Disabled setters
    override fun setFrame(x: kotlin.Double, y: kotlin.Double, w: kotlin.Double, h: kotlin.Double) = throw IllegalStateException()
    override fun setFrame(loc: Point2D?, size: Dimension2D?) = throw IllegalStateException()
    override fun setFrame(r: Rectangle2D?) = throw IllegalStateException()
    override fun setFrameFromDiagonal(x1: kotlin.Double, y1: kotlin.Double, x2: kotlin.Double, y2: kotlin.Double) = throw IllegalStateException()
    override fun setFrameFromDiagonal(p1: Point2D?, p2: Point2D?) = throw IllegalStateException()
    override fun setFrameFromCenter(centerX: kotlin.Double, centerY: kotlin.Double, cornerX: kotlin.Double, cornerY: kotlin.Double) = throw IllegalStateException()
    override fun setFrameFromCenter(center: Point2D?, corner: Point2D?) = throw IllegalStateException()
    override fun setRect(x: kotlin.Double, y: kotlin.Double, width: kotlin.Double, height: kotlin.Double) = throw IllegalStateException()
    override fun setRect(r: Rectangle2D?) = throw IllegalStateException()
    override fun add(newx: Int, newy: Int) = throw IllegalStateException()
    override fun add(pt: Point) = throw IllegalStateException()
    override fun add(r: Rectangle) = throw IllegalStateException()
    override fun add(newx: kotlin.Double, newy: kotlin.Double) = throw IllegalStateException()
    override fun add(pt: Point2D?) = throw IllegalStateException()
    override fun add(r: Rectangle2D?) = throw IllegalStateException()
    override fun setBounds(r: Rectangle) = throw IllegalStateException()
    override fun setBounds(x: Int, y: Int, width: Int, height: Int) = throw IllegalStateException()
    override fun reshape(x: Int, y: Int, width: Int, height: Int) = throw IllegalStateException()
    override fun setLocation(p: Point) = throw IllegalStateException()
    override fun setLocation(x: Int, y: Int) = throw IllegalStateException()
    override fun move(x: Int, y: Int) = throw IllegalStateException()
    override fun translate(dx: Int, dy: Int) = throw IllegalStateException()
    override fun setSize(d: Dimension) = throw IllegalStateException()
    override fun setSize(width: Int, height: Int) = throw IllegalStateException()
    override fun resize(width: Int, height: Int) = throw IllegalStateException()
    override fun grow(h: Int, v: Int) = throw IllegalStateException()
    //#endregion

    companion object {
        val DOT = Rect(Pt.ZERO, Dim.ONE)
    }
}

val Rectangle.pixels get() = width * height
val Rectangle.rect get() = Rect(x, y, width, height)
val Rectangle.text get() = "$x;$y ${width}x$height"

operator fun Rectangle.minus(point: Point) = Rect(x - point.x, y - point.y, size)
operator fun Rectangle.plus(point: Point) = Rect(x + point.x, y + point.y, size)
//#endregion
