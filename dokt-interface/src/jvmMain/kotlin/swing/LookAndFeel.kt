@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.common.*
import app.dokt.common.Dimension
import app.dokt.common.Rectangle
import jiconfont.IconCode
import java.awt.*
import javax.swing.*

interface LookAndFeelData {
    val className get() = "javax.swing.plaf.${id.lowercase()}.${id}LookAndFeel"

    /** Decorated frame supports transparency */
    val decoratedTransparency: Boolean

    /** Internal or external frame border thickness */
    val frameBorder: Int

    /** Internal or external frame border and title bar height */
    val frameBorderAndTitle: Int

    /** Internal or external frame border, title bar and menu bar height */
    val frameBorderTitleAndMenu: Int

    val id: String

    /** Icon size in pixels */
    val iconSize: Float

    /** Internal or external frame menu bar height */
    val menu get() = frameBorderTitleAndMenu - frameBorderAndTitle

    /** Internal or external frame title bar height */
    val title get() = frameBorderAndTitle - frameBorder
}

object Metal : LookAndFeelData {
    override val decoratedTransparency = true
    override val frameBorder = 5
    override val frameBorderAndTitle = 28
    override val frameBorderTitleAndMenu = 51
    override val id = "Metal"
    override val iconSize = 15f
}

/**
 * https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html
 */
object Nimbus : LookAndFeelData {
    /** https://community.oracle.com/tech/developers/discussion/1358501/jinternalframe-transparency-issues-with-nimbus-look-and-feel */
    override val decoratedTransparency = false
    override val frameBorder = 5 // TODO
    override val frameBorderAndTitle = 28 // TODO
    override val frameBorderTitleAndMenu = 51 // TODO
    override val id = "Nimbus"
    override val iconSize = 15f // TODO
}

var currentLookAndFeel: LookAndFeelData = Metal

private fun boundsByContent(content: Rectangle, menuBar: JMenuBar?): Rectangle = with(currentLookAndFeel) {
    val top = if (menuBar == null) frameBorderAndTitle else frameBorderTitleAndMenu
    return Rectangle(
        content.x - frameBorder,
        content.y - top,
        sizeByContent(content.size, menuBar)
    )
}

private fun sizeByContent(content: Dimension, menuBar: JMenuBar?): Dimension = with(currentLookAndFeel) {
    val top = if (menuBar == null) frameBorderAndTitle else frameBorderTitleAndMenu
    return Dimension(
        content.width + 2 * frameBorder,
        content.height + top + frameBorder
    )
}

fun JFrame.setBoundsByContent(content: Rectangle) { bounds = boundsByContent(content, jMenuBar) }
fun JFrame.setSizeByContent(content: Dimension) { size = sizeByContent(content, jMenuBar) }

/** Default size for Windows 11 */
fun JFrame.setIcons(code: IconCode) { iconImages = listOf(code.image, IconBuilder.image(code, 63f)) }

fun JInternalFrame.setBoundsByContent(content: Rectangle) { bounds = boundsByContent(content, jMenuBar) }
fun JInternalFrame.setSizeByContent(content: Dimension) { size = sizeByContent(content, jMenuBar) }
