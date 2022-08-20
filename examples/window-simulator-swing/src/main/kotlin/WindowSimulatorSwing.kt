package fi.papinkivi.simulator

import app.dokt.ui.swing.*
import java.util.*
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JList

object WindowSimulatorFrame : Frame(null, "Window simulator", Application) {
    val layout = BoxLayout(contentPane, BoxLayout.Y_AXIS).apply { contentPane.layout = this}

    val windowTable = addTable({ Windows.all }) {
        column("icon", { icon }) { preferredWidth = 50 }
        column("title", { title }) { preferredWidth = 400 }
        column("process", { path }) { preferredWidth = 400 }
        column("location", { "$x;$y" }) { preferredWidth = 75 }
        column("size", { "$width, $height" }) { preferredWidth = 75 }
        rowHeight = 50
        selectionListener = {
            println(it.id)
        }
        configure = {
            setPreferredSize(1000, 1000)
        }
    }
    val imageList = JList<String>(DefaultListModel<String?>().apply { addAll(listOf("foo", "bar", "baz")) }).apply {
        this@WindowSimulatorFrame.add(this)
    }

    init {
        pack()
    }

    override fun localize(locale: Locale) {}
}

object WindowSimulatorSwing : SwingUI<WindowSimulator, WindowSimulatorFrame>(true) {
    override val application = WindowSimulator

    override fun createFrame() = WindowSimulatorFrame
}

fun main(args: Array<String>) {
    WindowSimulatorSwing.arguments = args
    WindowSimulatorSwing.start()
}
