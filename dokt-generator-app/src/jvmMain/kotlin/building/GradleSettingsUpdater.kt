package app.dokt.generator.building

import app.dokt.common.addAfterContains
import app.dokt.common.anyContains
import app.dokt.generator.REFRESH_VERSIONS_PLUGIN_ID
import app.dokt.generator.vRefreshVersions
import java.io.File

/**
 * Gradle Kotlin settings script updater
 */
class GradleSettingsUpdater(dir: File = File(".")) : FileLinesUpdater(dir, {}) {
    override val extension get() = EXTENSION

    override val name get() = NAME

    private val refreshLine = """    id("$REFRESH_VERSIONS_PLUGIN_ID") version "$vRefreshVersions""""

    override fun update(previous: List<String>?) = update(previous, directory.name)

    fun update(lines: List<String>?, rootProjectName: String) : List<String>? {
        val updated = lines?.toMutableList() ?: mutableListOf()
        var changed = lines == null

        if (updated.anyContains(REFRESH_VERSIONS_PLUGIN_ID)) debug { "$REFRESH_VERSIONS_PLUGIN_ID found" }
        else {
            info { "Adding $REFRESH_VERSIONS_PLUGIN_ID plugin" }
            if (updated.addAfterContains("app.dokt", refreshLine) < 0) kotlin.error("Plugin dependency not found!")
            else changed = true
        }

        if (updated.anyContains(ROOT_NAME)) debug { "$ROOT_NAME found" }
        else {
            val statement = """$ROOT_NAME = "$rootProjectName""""
            info { "Adding '$statement'" }
            updated.add("")
            updated.add(statement)
            changed = true
        }

        return if (changed) updated else null
    }

    companion object {
        private const val EXTENSION = "gradle.kts"
        private const val NAME = "settings"
        const val FILENAME = "$NAME.$EXTENSION"
        private const val ROOT_NAME = "rootProject.name"
    }
}
