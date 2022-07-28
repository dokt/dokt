package fi.papinkivi.simulator

import app.dokt.ui.swing.*
import java.awt.image.BufferedImage
import java.util.*

class WindowSimulatorFrame : Frame(title = "Window simulator") {

    init {
        addTable(*Windows.all.toTypedArray()) {
            column("icon", { icon }) { preferredWidth = 10 }
            column("title", { title }) { preferredWidth = 150 }
            column("process", { path }) { preferredWidth = 150 }
            column("location", { "$x;$y" }) { preferredWidth = 30 }
            column("size", { "$width, $height" }) { preferredWidth = 30 }
            render<BufferedImage>(ImageView())
            rowHeight = 50
            selectionListener = {
                println(it.id)
            }
        }
        pack()
    }

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
