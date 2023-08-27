package app.dokt.generator.building.gradle

import app.dokt.app.Repository
import app.dokt.common.Version

@Repository
interface SettingsInitialization {
    fun applyPlugin(id: String, version: Version)
}
