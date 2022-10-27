package app.dokt.ui

import app.dokt.app.Application
import app.dokt.infra.SystemJvm
import java.util.*

private val logger = mu.KotlinLogging.logger {}

abstract class JavaUI<A : Application>(func: () -> Unit, private val bundleBaseName: String? = null)
    : ConsoleUI<A>(func), Localized {
    companion object {
        lateinit var instance: JavaUI<*>
    }

    private val defaultLocale: Locale = Locale.getDefault()
    var locale = defaultLocale
        set(value) {
            if (value == field) return
            logger.info { "Changing locale from $field to $value." }
            field = value
            Locale.setDefault(value)
            localize(value)
        }

    init { initInstance() }

    private fun initInstance() { instance = this }

    override fun localize(locale: Locale) {
        bundleBaseName?.let {
            logger.debug { "Loading resources for $locale locale." }
            bundle = ResourceBundle.getBundle(it, locale)
        }
    }

    override fun start() {
        super.start()
        logger.debug { "Setting system properties." }
        defineSystemProperties()
        logger.debug { "Default locale is $defaultLocale." }
        localize(locale)
    }

    open fun defineSystemProperties() {
        logger.debug { "No need to set system properties." }
    }

    protected fun defineSystemProperty(key: String, value: Any) = SystemJvm.defineProperty(key, value)
}
