package app.dokt.infra

import app.dokt.common.Point
import app.dokt.common.Rectangle

/** Take screenshot, capture area of single pixel of it. */
expect object Capture {
    /** Take screenshot of the whole screen. */
    operator fun invoke(): Image

    /** Capture area of the screen. */
    operator fun invoke(area: Rectangle): Image

    ///** Take screenshot of the whole screen or a single display. */
    //operator fun invoke(displayIndex: Int? = null): Image

    /** Get color of single pixel. */
    operator fun invoke(point: Point): Color
}
