@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.common.upperFirst
import app.dokt.ui.*
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.*

class Locales(vararg locales: Locale) : Menu(locales.map { SetLocale(it) }, GoogleMaterialDesignIcons.LANGUAGE)

class MenuBar(private vararg val menus: Menu): JMenuBar(), Localized {
    init { menus.forEach { add(it) } }

    override fun localize(locale: Locale) { menus.forEach { it.localize(locale) } }
}

class SetLocale(private val locale: Locale) : Action(
    name = locale.nativeDisplayName.upperFirst,
    localizeName = false
) {
    override fun actionPerformed(e: ActionEvent?) { JavaUI.instance.locale = locale }

    override fun localize(locale: Locale) { isEnabled = locale != this.locale }
}
