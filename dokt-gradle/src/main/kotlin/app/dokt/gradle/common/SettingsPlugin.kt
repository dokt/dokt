package app.dokt.gradle.common

import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion
import kotlin.reflect.KClass

abstract class SettingsPlugin(private val type: KClass<out SettingsPlugin>) : LoggablePlugin<Settings>(type) {
    protected abstract val minimum: GradleVersion

    final override fun Settings.validate() {
        val current = GradleVersion.current()
        require(current >= minimum) {
            "The ${type.qualifiedName} requires at least $minimum! Current version is $current." }
    }
}
