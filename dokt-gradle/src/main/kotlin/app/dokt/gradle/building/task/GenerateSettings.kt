package app.dokt.gradle.building.task

import app.dokt.generator.building.GradleProject
import app.dokt.generator.building.GradleSettingsWriter
import app.dokt.gradle.common.task.LoggableTask
import org.gradle.api.tasks.TaskAction

abstract class GenerateSettings : LoggableTask(GenerateSettings::class, "Generate settings.gradle.kts file!") {
    @TaskAction
    fun generate() {
        GradleProject.parse(project.projectDir.toPath(), name = project.name).let { project ->
            GradleSettingsWriter(project).generate()
        }
        didWork = true
    }
}
