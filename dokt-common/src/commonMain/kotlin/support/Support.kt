package app.dokt.common.support

import app.dokt.common.*
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

expect abstract class Point2D {
    fun distance(point: Point2D): Double
    fun distance(px: Double, py: Double): Double
}

object DimensionSerializer : ClassSerializer<Dimension>("Dimension", {
    element<Int>("width")
    element<Int>("height")
}) {
    private const val WIDTH = 0
    private const val HEIGHT = 1

    override fun CompositeDecoder.deserialize() = Dimension().apply {
        while (true) {
            when (val index = decodeIndex) {
                WIDTH -> width = decodeInt(WIDTH)
                HEIGHT -> height = decodeInt(HEIGHT)
                DECODE_DONE -> break
                else -> unexpected(index)
            }
        }
    }

    override fun CompositeEncoder.serialize(value: Dimension) {
        encodeIntElement(descriptor, WIDTH, value.width)
        encodeIntElement(descriptor, HEIGHT, value.height)
    }
}

//@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = Point::class) and @file:UseSerializers(PointSerializer::class) doesn't work!
object PointSerializer : ClassSerializer<Point>("Point", {
    element<Int>("x")
    element<Int>("y")
}) {
    private const val X = 0
    private const val Y = 1

    override fun CompositeDecoder.deserialize() = Point().apply {
        while (true) {
            when (val index = decodeIndex) {
                X -> x = decodeInt(X)
                Y -> y = decodeInt(Y)
                DECODE_DONE -> break
                else -> unexpected(index)
            }
        }
    }

    override fun CompositeEncoder.serialize(value: Point) {
        encodeIntElement(descriptor, X, value.x)
        encodeIntElement(descriptor, Y, value.y)
    }
}

object RectangleSerializer : ClassSerializer<Rectangle>("Point", {
    element<Int>("x")
    element<Int>("y")
    element<Int>("width")
    element<Int>("height")
}) {
    private const val X = 0
    private const val Y = 1
    private const val WIDTH = 2
    private const val HEIGHT = 3

    override fun CompositeDecoder.deserialize() = Rectangle().apply {
        while (true) {
            when (val index = decodeIndex) {
                X -> x = decodeInt(X)
                Y -> y = decodeInt(Y)
                WIDTH -> width = decodeInt(WIDTH)
                HEIGHT -> height = decodeInt(HEIGHT)
                DECODE_DONE -> break
                else -> unexpected(index)
            }
        }
    }

    override fun CompositeEncoder.serialize(value: Rectangle) {
        encodeIntElement(descriptor, X, value.x)
        encodeIntElement(descriptor, Y, value.y)
        encodeIntElement(descriptor, WIDTH, value.width)
        encodeIntElement(descriptor, HEIGHT, value.height)
    }
}
