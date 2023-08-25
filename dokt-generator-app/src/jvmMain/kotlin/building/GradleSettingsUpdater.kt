package app.dokt.generator.building

import app.dokt.common.*
import app.dokt.generator.REFRESH_VER
import java.io.File

/**
 * Gradle Kotlin settings script updater
 */
class GradleSettingsUpdater(dir: File) : FileLinesUpdater(dir, {}) {
    override val extension get() = EXTENSION

    override val name get() = NAME

    private val refreshLine = """    id("$REFRESH_ID") version "$REFRESH_VER""""

    override fun update(previous: List<String>?) = update(previous, directory.name)

    fun update(lines: List<String>?, rootProjectName: String) : List<String>? {
        val updated = lines?.toMutableList() ?: mutableListOf()
        var changed = lines == null

        if (updated.anyContains(REFRESH_ID)) debug { "$REFRESH_ID found" }
        else {
            info { "Adding $REFRESH_ID plugin" }
            if (updated.addAfterContains("app.dokt", refreshLine) < 0) error("Plugin dependency not found!")
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
        @Suppress("SpellCheckingInspection")
        private const val REFRESH_ID = "de.fayard.refreshVersions"
        private const val ROOT_NAME = "rootProject.name"
    }
}