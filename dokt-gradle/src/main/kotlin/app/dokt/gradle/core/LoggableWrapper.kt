package app.dokt.gradle.core

import org.gradle.api.logging.*
import kotlin.reflect.KClass

class LoggableWrapper(type: KClass<*>) : LoggableAdapter() {
    override val logger: Logger = Logging.getLogger(type.java)
}