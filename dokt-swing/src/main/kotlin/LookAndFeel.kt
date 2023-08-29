@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.common.Dimension
import app.dokt.common.Rectangle
import jiconfont.IconCode
import javax.swing.JFrame
import javax.swing.JInternalFrame
import javax.swing.JMenuBar

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

private const val METAL_FRAME_BORDER = 5
private const val METAL_FRAME_TITLE_OR_MENU = 23
private const val METAL_ICON_SIZE = 15f
object Metal : LookAndFeelData {
    override val decoratedTransparency = true

    /** 5 px */
    override val frameBorder = METAL_FRAME_BORDER

    /** 5 + 23 = 28 px */
    override val frameBorderAndTitle = METAL_FRAME_BORDER + METAL_FRAME_TITLE_OR_MENU

    /** 5 + 2 * 23 = 51 px */
    override val frameBorderTitleAndMenu = METAL_FRAME_BORDER + 2 * METAL_FRAME_TITLE_OR_MENU

    override val id = "Metal"

    /** 15f */
    override val iconSize = METAL_ICON_SIZE
}

/** https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html */
object Nimbus : LookAndFeelData {
    /**
     * https://community.oracle.com/tech/developers/discussion/1358501/jinternalframe-transparency-issues-with-nimbus-look-and-feel
     */
    override val decoratedTransparency = false
    override val frameBorder = Metal.frameBorder // TODO
    override val frameBorderAndTitle = Metal.frameBorderTitleAndMenu // TODO
    override val frameBorderTitleAndMenu = Metal.frameBorderTitleAndMenu // TODO
    override val id = "Nimbus"
    override val iconSize = Metal.iconSize // TODO
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
private const val WINDOWS_11_ICON_SIZE = 63f
fun JFrame.setIcons(code: IconCode) { iconImages = listOf(code.image, Icons.image(code, WINDOWS_11_ICON_SIZE)) }

fun JInternalFrame.setBoundsByContent(content: Rectangle) { bounds = boundsByContent(content, jMenuBar) }
fun JInternalFrame.setSizeByContent(content: Dimension) { size = sizeByContent(content, jMenuBar) }
