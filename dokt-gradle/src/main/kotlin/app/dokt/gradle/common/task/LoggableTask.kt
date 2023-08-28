package app.dokt.gradle.common.task

import app.dokt.gradle.common.Loggable
import app.dokt.gradle.common.LoggableWrapper
import org.gradle.api.DefaultTask
import kotlin.reflect.KClass

abstract class LoggableTask(type: KClass<out LoggableTask>, private val description: String? = null) : DefaultTask(),
    Loggable by LoggableWrapper(type) {
    override fun getDescription() = description

    final override fun getGroup() = "dokt"
}
