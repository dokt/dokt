package app.dokt.gradle.common.task

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import kotlin.reflect.KClass

@CacheableTask
abstract class Generate(type: KClass<out Generate>, description: String? = null) : LoggableTask(type, description) {
    @get:OutputDirectory
    abstract val generated: DirectoryProperty

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val source: DirectoryProperty

    @TaskAction
    abstract fun generate()
}