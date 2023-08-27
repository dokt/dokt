package app.dokt.gradle.common

import org.gradle.api.Plugin
import kotlin.reflect.KClass

abstract class LoggablePlugin<T : Any>(private val type: KClass<out LoggablePlugin<T>>) :
    Loggable by LoggableWrapper(type), Plugin<T>
{
    protected lateinit var pluginTarget: T
        private set

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    final override fun apply(target: T) {
        this.pluginTarget = target
        target.validate()
        debug { "Applying plugin to $target." }
        target.applyPlugin()
    }

    protected open fun T.validate() {
        debug { "No validation defined." }
    }

    protected abstract fun T.applyPlugin()
}
