package fi.papinkivi.simulator

import app.dokt.app.Application



class WindowInfo(
    val id: Int,
    val title: String,
    /** Process path */
    val path: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val handle: Any
) {
    val visible get() = /*x != 0 && y != 0 && */ width > 0 && height > 0

    override fun equals(other: Any?) = other is WindowInfo && other.id == id
}

object WindowSimulator : Application()

expect object Windows {
    val all: List<WindowInfo>
}