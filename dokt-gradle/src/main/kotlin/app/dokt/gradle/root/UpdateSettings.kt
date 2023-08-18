package app.dokt.gradle.root

import app.dokt.common.*
import app.dokt.gradle.REFRESH_VER
import app.dokt.gradle.core.LoggableTask
import org.gradle.api.tasks.TaskAction

abstract class UpdateSettings : LoggableTask(UpdateSettings::class, "Update $FILENAME file.") {
    private val file by lazy { root.resolve(FILENAME) }

    private val root by lazy { project.projectDir }

    @TaskAction
    fun update() {
        lifecycle { "Updating $FILENAME" }
        val lines = read().toMutableList()
        var changed = false

        if (lines.anyContains(REFRESH_ID)) debug { "$REFRESH_ID found" }
        else {
            info { "Adding $REFRESH_ID plugin" }
            if (lines.addAfterContains("app.dokt", REFRESH_LINE) < 0) error("Plugin dependency not found!")
            else changed = true
        }

        if (lines.anyContains(ROOT_NAME)) debug { "$ROOT_NAME found" }
        else {
            val statement = """$ROOT_NAME = "${root.name}""""
            info { "Adding '$statement'" }
            lines.add("")
            lines.add(statement)
            changed = true
        }

        if (changed) write(lines)
        else info { "$FILENAME not modified" }
    }

    private fun read(): List<String> {
        if (!file.exists()) throw UnsupportedOperationException("Updating settings requires $FILENAME file!")
        debug { "Reading $FILENAME" }
        return file.readLines()
    }

    private fun write(lines: List<String>) {
        debug { "Writing $FILENAME" }
        file.writeLines(lines)
    }

    companion object {
        @Suppress("SpellCheckingInspection")
        private const val REFRESH_ID = "de.fayard.refreshVersions"
        private const val REFRESH_LINE = """    id("$REFRESH_ID") version "$REFRESH_VER""""
        private const val ROOT_NAME = "rootProject.name"
        const val FILENAME = "settings.gradle.kts"
    }
}