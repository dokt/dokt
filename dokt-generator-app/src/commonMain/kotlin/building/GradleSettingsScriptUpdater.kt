package app.dokt.generator.building

import app.dokt.common.Version

interface GradleSettingsScriptUpdater {
    /** Adds or updates plugin. */
    fun addPlugin(id: String, version: Version)
}
