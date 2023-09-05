package app.dokt.gradle.building.task

import app.dokt.generator.building.gradle.SettingsFileUpdater
import app.dokt.gradle.common.path
import app.dokt.gradle.core.task.DoktServiceTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.readText
import kotlin.io.path.writeText

@CacheableTask
abstract class UpdateSettings :
    DoktServiceTask(
        UpdateSettings::class,
        Group.BuildSetup,
        "Updates Gradle settings script file.") {

    @get:Input
    abstract val modified: Property<Long>

    @get:OutputFile
    abstract val file: RegularFileProperty

    @TaskAction
    fun update() {
        val path = file.path
        SettingsFileUpdater(path.readText()).apply {
            didWork = update(project.name, doktService.subprojectsToInclude)
            if (didWork) path.writeText(content)
        }
    }
}
