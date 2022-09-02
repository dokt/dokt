package fi.papinkivi.window

import app.dokt.common.*
import com.sun.jna.platform.WindowUtils
import java.awt.Robot


object JvmScreen : Screen {
    override val captureExtension = PNG

    private var handles = getHandles()
    private val robot = Robot()

    override fun capture(area: Rect) = robot.createScreenCapture(area).writePng()

    override fun measure(id: WindowId) = handles[id]?.let { WindowUtils.getWindowLocationAndSize(it)?.rect }

    private fun getHandles() = WindowUtils.getAllWindows(false)
        .associate { WindowId(it.filePath, it.title) to it.hwnd!! }

    fun inject() { Window.screen = this }
}