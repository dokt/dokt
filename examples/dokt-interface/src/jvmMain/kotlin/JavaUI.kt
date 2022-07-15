package app.dokt.ui

import app.dokt.app.Application
import java.util.*

private val logger = mu.KotlinLogging.logger {}

abstract class JavaUI<A : Application>(private val bundleBaseName: String? = null)
    : ConsoleUI<A>(), Localized {
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

    protected fun defineSystemProperty(key: String, value: Any) {
        val old = System.getProperty(key)
        if (value == old) logger.info { "'$key' system property '$value' value keep unchanged." }
        else {
            System.setProperty(key, value.toString())
            logger.info { "'$key' system property value changed from '$old' to '$value'." }
        }
    }
}
