@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.common.get
import app.dokt.ui.*
import jiconfont.IconCode
import java.util.*
import javax.swing.JMenu

sealed interface MenuComponent : Localized

open class Menu(
    private val components: List<MenuComponent>,
    code: IconCode? = null,
    name: String? = null,
    label: String = "",
    disable: Boolean = false
) : JMenu(label), MenuComponent {
    constructor(vararg components: MenuComponent) : this(components.toList())

    init {
        icon = code?.icon
        if (name != null) {
            this.name = name
        } else {
            if (label.isBlank()) {
                this.name = javaClass.simpleName
            }
        }
        this.name?.let { text = bundle.get(it) }
        components.forEach {
            when (it) {
                is Action -> add(it)
                is Menu -> add(it)
                is Separator -> addSeparator()
            }
        }
        isEnabled = !disable
    }

    final override fun localize(locale: Locale) {
        this.name?.let { text = bundle.get(it) }
        components.forEach { it.localize(locale) }
    }
}

/** https://docs.oracle.com/javase/tutorial/uiswing/components/separator.html */
object Separator : MenuComponent { override fun localize(locale: Locale) {} }
