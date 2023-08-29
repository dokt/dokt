@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.common.area
import app.dokt.common.DOT
import app.dokt.common.Dimension
import app.dokt.common.size
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

private const val PREFERRED_SIZE = 100

class ImageView(image: BufferedImage = DOT) : JComponent(), TableCellRenderer {
    var image = image
        set(value) {
            field = value
            size = value.size
            repaint()
        }

    init {
        image.size.let {
            if (it.area < PREFERRED_SIZE) preferredSize = Dimension(PREFERRED_SIZE, PREFERRED_SIZE)
            else size = it
        }
    }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ) = apply { image = value?.let { it as BufferedImage } ?: DOT }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, null)
    }
}

open class ImageViewFrame(title: String? = null, image: BufferedImage = DOT) : Frame(title) {
    private val view = ImageView(image)

    var image by view::image

    init {
        isResizable = false
        contentPane = view
    }
}

open class ImageViewInternalFrame(
    title: String? = null,
    image: BufferedImage = DOT,
    closable: Boolean = true,
    iconifiable: Boolean = true
) : InternalFrame(title, false, closable, false, iconifiable) {
    private val viewer = ImageView(image)

    var image by viewer::image

    init { contentPane = viewer }
}
