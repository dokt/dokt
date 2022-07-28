package fi.papinkivi.simulator

import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.WinDef
import java.awt.image.BufferedImage

actual object Windows {
    actual val all get() = WindowUtils.getAllWindows(true)
        .map {
            with(it.locAndSize) {
                val handle = it.hwnd
                WindowInfo(Integer.decode(handle.toString().substringAfter('@')), it.title, it.filePath, x, y, width, height, handle)
            }
        }
        .filter { it.visible && !it.path.contains("explorer") }
}

val WindowInfo.icon: BufferedImage? get() = WindowUtils.getWindowIcon(handle as WinDef.HWND)