package app.dokt.gradle.core

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.work.*
import java.io.File
import kotlin.reflect.KClass

@CacheableTask
abstract class UpdateFile(type: KClass<out UpdateFile>, description: String? = null) : LoggableTask(type, description) {
    @get:Incremental
    @get:InputFile
    //@get:OutputFile
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