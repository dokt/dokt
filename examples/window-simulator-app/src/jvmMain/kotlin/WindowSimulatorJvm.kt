package fi.papinkivi.simulator

import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.WinDef
import javax.swing.ImageIcon

actual object Windows {
    actual val all get() = WindowUtils.getAllWindows(true)
        .map {
            with(it.locAndSize) {
                val handle = it.hwnd
                WindowInfo(Integer.decode(handle.toString().substringAfter('@')), it.title, it.filePath, x, y, width, height, handle)
            }
        }
        .filter { it.visible && !it.path.contains("explorer") }
        .sortedBy { it.id }
}

val WindowInfo.icon get() = WindowUtils.getWindowIcon(handle as WinDef.HWND)?.let { ImageIcon(it) }