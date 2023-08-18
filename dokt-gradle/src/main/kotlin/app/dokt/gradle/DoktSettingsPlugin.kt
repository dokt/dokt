package app.dokt.gradle

import app.dokt.gradle.core.SettingsPlugin
import app.dokt.gradle.root.RootProjectPlugin
import org.gradle.api.initialization.Settings
import org.gradle.util.GradleVersion

@Suppress("unused")
class DoktSettingsPlugin : SettingsPlugin(DoktSettingsPlugin::class) {
    override val minimum: GradleVersion = GradleVersion.version(MIN_GRADLE)

    /** Initialize settings. */
    override fun Settings.initialize() {
        lifecycle { "Initializing https://dokt.app" }

        debug { "Add Maven Central and local to every project repositories." }
        dependencyResolutionManagement {
            with(it.repositories) {
                mavenCentral()
                mavenLocal()
            }
        }

        debug { "Add action to root project when it's read." }
        gradle.rootProject {
            debug { "Add Dokt root project plugin." }
            it.pluginManager.apply(RootProjectPlugin::class.java)
        }

        // TODO validate settings file rootProject.name = rootDir.name This doesn't work in CI builds.
    }
}