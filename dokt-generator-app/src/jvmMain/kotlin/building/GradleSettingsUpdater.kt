package app.dokt.generator.building

import app.dokt.common.*
import java.io.File

/**
 * Gradle Kotlin settings script updater
 */
class GradleSettingsUpdater(private val path: File, refreshVer: String) :
    AbstractFileUpdater(path.resolve(FILENAME), {}) {

    private val refreshLine = """    id("$REFRESH_ID") version "$refreshVer""""

    override fun update() {
        if (!file.exists()) throw UnsupportedOperationException("Updating settings requires $FILENAME file!")
        debug { "Reading $file" }
        val lines = update(file.readLines(), path.name)
        if (lines == null) info { "$file not modified" }
        else {
            debug { "Writing $file" }
            file.writeLines(lines)
        }
    }

    fun update(lines: List<String>, rootProjectName: String) : List<String>? {
        val updated = lines.toMutableList()
        var changed = false

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

        return if (changed) updated
        else {
            info { "$FILENAME not modified" }
            null
        }
    }

    companion object {
        @Suppress("SpellCheckingInspection")
        private const val REFRESH_ID = "de.fayard.refreshVersions"
        private const val ROOT_NAME = "rootProject.name"
        const val FILENAME = "settings.gradle.kts"
    }
}