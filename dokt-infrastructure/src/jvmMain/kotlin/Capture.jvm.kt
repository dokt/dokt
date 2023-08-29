@file:Suppress("MatchingDeclarationName")

package app.dokt.infra

import app.dokt.common.Point
import app.dokt.common.Rectangle
import app.dokt.common.origin
import app.dokt.common.text
import java.awt.Robot
import kotlin.system.measureTimeMillis

actual object Capture : Logger({}) {
    private val robot: Robot

    init {
        if (SystemJvm.scale) SystemJvm.scale = false
        robot = Robot()
    }

    actual operator fun invoke(): Image {
        debug { "Capturing screen." }
        return invoke(Rectangle(origin, screenSize))
    }

    actual operator fun invoke(area: Rectangle): Image {
        debug { "Capturing ${area.text}." }
        val image: Image
        val time = measureTimeMillis {
            image = robot.createScreenCapture(area)
        }
        info { "Captured ${area.text} in $time ms." }
        return image
    }

    actual operator fun invoke(point: Point): Color {
        debug { "Getting pixel from ${point.text}." }
        val color: Color
        val time = measureTimeMillis {
            color = robot.getPixelColor(point.x, point.y)
        }
        info { "Got color ${color.text} in $time ms." }
        return color
    }
}
