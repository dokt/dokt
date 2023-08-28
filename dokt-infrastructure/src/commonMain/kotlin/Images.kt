@file:Suppress("unused")

package app.dokt.infra

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/** Adds dot prefix for file extensions. */
private fun dot(vararg extensions: String) = extensions.map { ".$it" }

/** Read-only image */
expect interface Image

val Image.text get() = "TODO"// TODO
fun Image.to(format: ImageFormat) = Images(this, format)
fun Image.toBmp() = to(ImageFormat.BMP)
fun Image.toGif() = to(ImageFormat.GIF)
fun Image.toJpeg() = to(ImageFormat.JPEG)
fun Image.toPng() = to(ImageFormat.PNG)

/** Image file format */
@Serializable
enum class ImageFormat(
    @Transient
    val description: String,

    @Transient
    /**
     * [Filename extensions](https://en.wikipedia.org/wiki/Filename_extension) containing first dot if exists in
     * preference order.
     */
    val extensions: List<String>,

    /** [Internet media types](https://en.wikipedia.org/wiki/Media_type) in preference order. */
    @Transient
    val types: List<String>
    ) {
    BMP("Windows Bitmap", dot("bmp", "dip"), "bmp", "x-bmp"),
    GIF("Graphics Interchange Format", "gif"),
    JPEG(
        "Joint Photographic Experts Group",
        dot("jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
        "jpeg"
    ),
    PNG("Portable Network Graphics", "png",),
    WEBP("WebP", "webp");

    /** Primary extension without the dot. Used in JVM. */
    val primarySuffix = extensions.first().substring(1)

    constructor(label: String, type: String) : this(label, dot(type), type)

    constructor(label: String, extensions: List<String>, vararg types: String) :
            this(label, extensions, types.map { "image/$it" })
}

/** Image reader and writer service */
expect object Images {
    operator fun invoke(data: ByteArray): Image
    operator fun invoke(image: Image, format: ImageFormat): ByteArray
}

/** Drawable image */
expect interface MutableImage : Image
