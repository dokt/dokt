package app.dokt.gradle.core

import org.gradle.api.DefaultTask
import kotlin.reflect.KClass

abstract class LoggableTask(type: KClass<out LoggableTask>, private val description: String? = null) : DefaultTask(),
    Loggable by LoggableWrapper(type) {
    final override fun getDescription() = description

    final override fun getGroup() = "dokt"
}