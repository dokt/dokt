package app.dokt.gradle.common

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import kotlin.reflect.KClass

class LoggableWrapper(type: KClass<*>) : LoggableAdapter() {
    override val logger: Logger = Logging.getLogger(type.java)
}
