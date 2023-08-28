@file:Suppress("MatchingDeclarationName")

package app.dokt.infra

import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.awt.image.WritableRenderedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

actual object Images : Logger({}) {
    actual operator fun invoke(data: ByteArray): Image {
        debug { "Reading image of ${data.size} B." }
        val image: BufferedImage
        val time = measureTimeMillis {
            image = ImageIO.read(ByteArrayInputStream(data))
        }
        info { "Read $image in $time ms." }
        return image
    }

    actual operator fun invoke(image: Image, format: ImageFormat): ByteArray {
        debug { "Writing $image to $format." }
        when (format) {
            ImageFormat.GIF,
            ImageFormat.PNG,
            ImageFormat.JPEG,
            ImageFormat.BMP,
            ImageFormat.WEBP -> {
                ByteArrayOutputStream().let {
                    val bytes: ByteArray
                    val time = measureTimeMillis {
                        ImageIO.write(image, format.primarySuffix, it)
                        bytes = it.toByteArray()
                    }
                    info { "Wrote $image to $format in $time ms using ${bytes.size} B." }
                    return bytes
                }
            }
            else -> throw UnsupportedOperationException()
        }
    }
}

/** JVM image wrapper */
actual typealias Image = RenderedImage

/** JVM mutable image wrapper */
actual typealias MutableImage = WritableRenderedImage
