package app.dokt.gradle.common

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.*
import java.io.File
import kotlin.reflect.KClass

// https://docs.gradle.org/current/userguide/incremental_build.html#sec:task_inputs_outputs
@CacheableTask
abstract class UpdateDir(type: KClass<out UpdateDir>, description: String? = null) : LoggableTask(type, description) {
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