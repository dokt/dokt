package app.dokt.generator.building.gradle

import app.dokt.app.Repository
import app.dokt.common.Version

@Repository
interface SettingsInitialization {
    var root: String?

    var projects: List<String>

    fun pluginsUseMavenLocal()

    fun applyPlugin(id: String, version: Version)

    fun manageDependencyResolutions(useMavenLocal: Boolean = false)

    companion object {
        const val FILE_NAME = "settings.gradle"
    }
}
