package app.dokt.gradle.core

import org.gradle.api.Plugin
import kotlin.reflect.KClass

abstract class LoggablePlugin<T : Any>(type: KClass<out LoggablePlugin<T>>) : Loggable by LoggableWrapper(type),
    Plugin<T> {
    protected lateinit var target: T
        private set

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    final override fun apply(target: T) {
        this.target = target
        debug { "Apply plugin to $target." }
        applyPlugin()
    }

    protected abstract fun applyPlugin()
}