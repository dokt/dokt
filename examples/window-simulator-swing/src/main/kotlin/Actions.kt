package fi.papinkivi.simulator

import app.dokt.ui.*
import app.dokt.ui.swing.*
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons
import java.awt.event.ActionEvent
import javax.swing.table.AbstractTableModel

object Application : Menu(DetectWindows, Separator, Locales(en, fi), Separator, Exit)

object DetectWindows : Action(GoogleMaterialDesignIcons.DESKTOP_WINDOWS) {
    override fun actionPerformed(e: ActionEvent?) {
        logger.info { "Refreshing windows" }
        (WindowSimulatorFrame.windowTable.model as AbstractTableModel).fireTableDataChanged()
    }
}