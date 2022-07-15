@file:Suppress("unused")

package app.dokt.ui.swing

import app.dokt.*
import app.dokt.ui.*
import java.util.*
import javax.swing.*

open class Frame(
    name: String? = null,
    /** Localize title if null or use it as fixed. */
    title: String? = null,
    vararg menus: Menu
) : JFrame(title ?: ""), Localized {
    init {
        name?.let { this.name = it }
        if (title == null) {
            if (name == null) this.name = javaClass.simpleName
            this.title = bundle.get(this.name)
        }
        if (menus.isNotEmpty()) jMenuBar = MenuBar(*menus)
    }

    override fun localize(locale: Locale) {
        if (name != null && !name.startsWith("frame")) title = bundle.get(name)
        (jMenuBar as? MenuBar)?.localize(locale)
    }
}

open class InternalFrame(
    /** Localize title if null or use it as fixed. */
    title: String? = null,
    resizable: Boolean = false,
    closable: Boolean = false,
    maximizable: Boolean = false,
    iconifiable: Boolean = false,
) : JInternalFrame(title ?: "", resizable, closable, maximizable, iconifiable), Localized {
    init {
        if (title == null) {
            name = javaClass.simpleName
            this.title = bundle.get(name)
        }
    }

    fun captureContent() = ROBOT.createScreenCapture(contentPane.boundsOnScreen)!!

    override fun localize(locale: Locale) { name?.let { title = bundle.get(it) } }
}

/**
 * https://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html
 * Nimbus doesn't allow transparent background if default look & feel decorated.
 */
open class TransparentDesktopFrame(name: String? = null, title: String? = null, vararg menus: Menu)
    : Frame(name, title, *menus) {
    init {
        background = TRANSPARENT
        contentPane = JDesktopPane().apply {
            background = TRANSPARENT
            isOpaque = false
        }
    }

    protected fun <F : JInternalFrame> add(frame: F): F {
        contentPane.add(frame)
        return frame
    }
}

open class TransparentInternalFrame(
    title: String? = null,
    resizable: Boolean = false,
    closable: Boolean = false,
    maximizable: Boolean = false,
    iconifiable: Boolean = false
) : InternalFrame(title, resizable, closable, maximizable, iconifiable) { init { background = TRANSPARENT } }
