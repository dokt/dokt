package app.dokt.gradle.root

import app.dokt.generator.building.GradleSettingsUpdater
import app.dokt.gradle.REFRESH_VER
import app.dokt.gradle.core.LoggableTask
import org.gradle.api.tasks.TaskAction

abstract class UpdateSettings : LoggableTask(UpdateSettings::class,
    "Update ${GradleSettingsUpdater.FILENAME} file.") {
    private val dir by lazy { project.projectDir.toPath()!! }
    private val updater by lazy { GradleSettingsUpdater(REFRESH_VER) }

    @TaskAction
    fun update() {
        lifecycle { "Updating ${GradleSettingsUpdater.FILENAME}" }
        updater.update(dir)
    }
}