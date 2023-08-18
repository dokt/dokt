package app.dokt.gradle.core

import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion
import kotlin.reflect.KClass

abstract class SettingsPlugin(private val type: KClass<out SettingsPlugin>) : LoggablePlugin<Settings>(type) {
    protected abstract val minimum: GradleVersion

    override fun applyPlugin() {
        val current = GradleVersion.current()
        if (current < minimum)
            error("The ${type.qualifiedName} requires at least $minimum! Current version is ${current.version}.")
        else {
            debug { "Initialize settings." }
            target.initialize()
        }
    }

    /** Initialize settings. */
    protected abstract fun Settings.initialize()
}