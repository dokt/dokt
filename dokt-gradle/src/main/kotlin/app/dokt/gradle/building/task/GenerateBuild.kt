package app.dokt.gradle.building.task

import app.dokt.generator.building.*
import app.dokt.gradle.common.task.LoggableTask
import org.gradle.api.tasks.TaskAction

abstract class GenerateBuild : LoggableTask(GenerateBuild::class, "Generate build.gradle.kts files!") {
    @TaskAction
    fun generate() {
        GradleProject.parse(project.projectDir.toPath(), name = project.name).let { project ->
            GradleBuildWriter(project).write()
        }
        didWork = true
    }
}
