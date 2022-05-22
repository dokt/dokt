package fi.papinkivi.simulator

import app.dokt.iface.swing.*
import java.util.*

class WindowSimulatorFrame : Frame(title = "Window simulator") {
    override fun localize(locale: Locale) {}
}

object WindowSimulatorSwing : SwingUI<WindowSimulator, WindowSimulatorFrame>(true) {
    override val application = WindowSimulator

    override fun createFrame() = WindowSimulatorFrame()
}

fun main(args: Array<String>) {
    WindowSimulatorSwing.arguments = args
    WindowSimulatorSwing.start()
}
