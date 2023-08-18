@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package app.dokt.ui.swing

import app.dokt.common.*
import app.dokt.ui.*
import io.github.oshai.kotlinlogging.KotlinLogging
import jiconfont.IconCode
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons
import java.awt.event.*
import java.util.*
import javax.swing.*

/**
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/action.html
 * https://www.rd.com/article/computer-f-keys/
 */
abstract class Action(
    code: IconCode? = null,
    enabled: Boolean = true,
    accelerator: KeyStroke? = null,
    name: String? = null,
    private val localizeName: Boolean = true
) : AbstractAction(name), MenuComponent {
    protected val logger = KotlinLogging.logger { }
    private val name = name ?: javaClass.simpleName

    init {
        putValue(ACCELERATOR_KEY, accelerator)
        if (!localizeName) putValue(NAME, name)
        isEnabled = enabled
        code?.let { putValue(SMALL_ICON, it.icon) }
        init()
    }

    fun init() { localize() }

    override fun actionPerformed(e: ActionEvent?) {
        logger.info { "Action performed" }
    }

    open fun disable() { isEnabled = false }

    open fun enable() { isEnabled = true }

    override fun localize(locale: Locale) {
        if (localizeName) putValue(NAME, bundle.get(name))
        bundle.find("$name.d")?.let { putValue(SHORT_DESCRIPTION, it) }
    }

    final override fun putValue(key: String, newValue: Any?) { super.putValue(key, newValue) }

    open fun toggle(vararg with: Action) {
        isEnabled = !isEnabled
        with.forEach { it.toggle() }
    }
}

object Exit : Action(
    GoogleMaterialDesignIcons.EXIT_TO_APP,
    accelerator = keyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK)) {
    override fun actionPerformed(e: ActionEvent?) { JavaUI.instance.stop() }
}
