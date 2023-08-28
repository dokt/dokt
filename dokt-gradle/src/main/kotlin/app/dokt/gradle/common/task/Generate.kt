package app.dokt.gradle.common.task

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
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
