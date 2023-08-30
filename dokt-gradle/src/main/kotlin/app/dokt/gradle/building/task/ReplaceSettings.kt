package app.dokt.gradle.building.task

import app.dokt.generator.building.gradle.KotlinPoetSettingsInitializationScript
import app.dokt.generator.building.gradle.SettingsInitializer.initialize
import app.dokt.gradle.common.task.LoggableTask
import org.gradle.api.tasks.TaskAction

abstract class ReplaceSettings : LoggableTask(ReplaceSettings::class, "Replace settings.gradle.kts file!") {
    @TaskAction
    fun replace() {
        KotlinPoetSettingsInitializationScript().initialize(
            project.name,
            TODO()
        )
        didWork = true
    }
}
