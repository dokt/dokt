package app.dokt.common

import java.awt.*
import java.awt.image.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.io.path.div
import kotlin.math.roundToInt

actual typealias Dimension = java.awt.Dimension
actual typealias Point = java.awt.Point
actual typealias Line = java.awt.geom.Line2D.Double
actual typealias Rectangle = java.awt.Rectangle

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

fun RenderedImage.writePng(): ByteArray {
    val output = ByteArrayOutputStream(width * height)
    ImageIO.write(this, PNG, output)
    return output.toByteArray()
}

fun RenderedImage.writePng(file : File) = ImageIO.write(this, PNG, file)

fun RenderedImage.writePng(path : Path, filename: String): File =
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

val Raster.lastPixel: IntArray get() = getPixel(width - 1, height - 1, IntArray(numBands))
//#endregion
//#endregion
