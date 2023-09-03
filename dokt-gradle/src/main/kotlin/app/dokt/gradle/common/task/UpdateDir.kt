package app.dokt.gradle.common.task

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import kotlin.reflect.KClass

// https://docs.gradle.org/current/userguide/incremental_build.html#sec:task_inputs_outputs
@CacheableTask
abstract class UpdateDir(type: KClass<out UpdateDir>, description: String) :
    LoggableTask(type, Group.Build, description) {
    @get:Incremental
    @get:OutputFile // Output is required at minimum
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val generated: RegularFileProperty

    @get:Incremental
    @get:OutputFile // Output is required at minimum
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val file: RegularFileProperty

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        inputChanges.getFileChanges(file).forEach { change ->
            info { "${change.normalizedPath} ${change.changeType}" }
            change.file.changed()
        }
    }

    abstract fun File.changed()
}
