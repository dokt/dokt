package app.dokt.ui.swing

import javax.swing.*

fun main() {
    SwingUtilities.invokeLater { JFrame("Default").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(300, 100)
        isVisible = true
    } }
    SwingUtilities.invokeLater { JFrame("Undecorated").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setBounds(300, 0, 300, 100)
        isUndecorated = true
        isVisible = true
    } }
    SwingUtilities.invokeLater {
        JFrame.setDefaultLookAndFeelDecorated(true)
        JFrame("L&F decorated").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(600, 0, 300, 100)
            isVisible = true
        }
    }
}
