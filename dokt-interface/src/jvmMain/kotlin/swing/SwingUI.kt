@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package app.dokt.ui.swing

import app.dokt.app.Application
import app.dokt.infra.SystemJvm
import app.dokt.ui.*
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.event.*
import java.util.*
import javax.swing.*

private val logger = KotlinLogging.logger {}

/**
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/coroutines-guide-ui.md
 */
abstract class SwingUI<A : Application, F>(
    func: () -> Unit,
    private var scale: Boolean = true,
    private val defaultLookAndFeelDecorated: Boolean = false,
    private val lookAndFeel: LookAndFeelData = currentLookAndFeel
) : JavaUI<A>(func, "a"), Runnable where F : JFrame, F : Localized {
    lateinit var frame: F

    protected abstract fun createFrame(): F

    override fun defineSystemProperties() { if (SystemJvm.scale != scale) SystemJvm.scale = scale }

    override fun handleArgument(index: Int, argument: String) = if (argument == "scale=1") {
        scale = true
        true
    } else false

    override fun localize(locale: Locale) {
        super.localize(locale)
        if (::frame.isInitialized) frame.localize(locale)
    }

    override fun run() {
        logger.debug { "Creating main frame." }
        frame = createFrame()
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) { stop() }
        })
        logger.debug { "Showing main frame." }
        frame.isVisible = true
    }

    override fun start() {
        super.start()
        if (defaultLookAndFeelDecorated) {
            logger.info { "Decorating default Look and Feel." }
            JFrame.setDefaultLookAndFeelDecorated(true)
        }
        logger.debug { "Using ${lookAndFeel.id} Look and Feel" }
        if (lookAndFeel != currentLookAndFeel) {
            currentLookAndFeel = lookAndFeel
            UIManager.setLookAndFeel(lookAndFeel.className)
        }
        if (logger.isDebugEnabled()) {
            val defaultScreen = localGraphicsEnvironment.defaultScreenDevice
            localGraphicsEnvironment.screenDevices.forEach {
                val mode = it.displayMode
                logger.debug { "Screen '${it.iDstring}', ${mode.width}x${mode.height}${if (it == defaultScreen) ", default" else ""}." }
            }

            logger.debug { "Starting UI." }
        }
        SwingUtilities.invokeLater(this)
    }

    override fun stop() {
        logger.debug { "Disposing frames." }
        JFrame.getFrames().forEach { it.dispose() }
        logger.info { "Application exited normally." }
    }
}
