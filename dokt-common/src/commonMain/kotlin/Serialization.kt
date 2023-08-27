package app.dokt.common

import app.dokt.common.support.DimensionSerializer
import app.dokt.common.support.PointSerializer
import app.dokt.common.support.RectangleSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.SerializersModule

val module = SerializersModule {
    contextual(Dimension::class, DimensionSerializer)
    contextual(Point::class, PointSerializer)
    contextual(Rectangle::class, RectangleSerializer)
}

/** https://stackoverflow.com/questions/66496506/kotlinx-serialization-of-built-in-classes */
abstract class ClassSerializer<T>(
    name: String,
    describe: ClassSerialDescriptorBuilder.() -> Unit,
) : KSerializer<T> {
    override val descriptor = buildClassSerialDescriptor(name, builderAction = describe)

    final override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
        deserialize()
    }

    protected fun unexpected(index: Int): Nothing = kotlin.error("Unexpected index: $index")

    final override fun serialize(encoder: Encoder, value: T) = encoder.encodeStructure(descriptor) {
        serialize(value)
    }

    /** [Example](Decoder.decodeElementIndex) */
    protected val CompositeDecoder.decodeIndex get() = decodeElementIndex(descriptor)

    protected fun CompositeDecoder.decodeInt(index: Int) = decodeIntElement(descriptor, index)

    abstract fun CompositeDecoder.deserialize(): T

    abstract fun CompositeEncoder.serialize(value: T)

    companion object {
        @JvmStatic
        protected val DECODE_DONE = CompositeDecoder.DECODE_DONE

        @JvmStatic
        protected val UNKNOWN_NAME = CompositeDecoder.UNKNOWN_NAME
    }
}
