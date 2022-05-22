package app.dokt.iface.swing

import app.dokt.app.Application
import app.dokt.iface.*
import java.awt.event.*
import java.util.*
import javax.swing.*

private val logger = mu.KotlinLogging.logger {}

/**
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/coroutines-guide-ui.md
 */
abstract class SwingUI<A : Application, F>(
    private var scaleDisabled: Boolean = false,
    private val defaultLookAndFeelDecorated: Boolean = false,
    private val lookAndFeel: LookAndFeelData = currentLookAndFeel
) : JavaUI<A>("a"), Runnable where F : JFrame, F : Localized {
    lateinit var frame: F

    protected abstract fun createFrame(): F

    override fun defineSystemProperties() { if (scaleDisabled) disableScale() }

    private fun defineSystemProperty2d(suffix: String, value: Any = false) =
        defineSystemProperty("sun.java2d.$suffix", value)

    private fun defineSystemPropertyWinScale(suffix: String, scale: Double = 1.0) =
        defineSystemProperty2d("win.uiScale$suffix", scale)

    /**
     * https://news.kynosarges.org/2019/03/24/swing-high-dpi-properties/
     */
    private fun disableScale() {
        defineSystemProperty2d("dpiaware")
        defineSystemProperty2d("uiScale", 1.0)
        defineSystemProperty2d("uiScale.enabled")
        defineSystemPropertyWinScale("")
        defineSystemPropertyWinScale("X")
        defineSystemPropertyWinScale("Y")
    }

    override fun handleArgument(index: Int, argument: String) = if (argument == "scale=1") {
        scaleDisabled = true
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
        if (logger.isDebugEnabled) {
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
