package app.dokt.generator.building

import com.squareup.kotlinpoet.FileSpec
import java.io.File

abstract class KotlinScriptGenerator(directory: File, func: () -> Unit) :
    FileWriter<FileSpec.Builder>(directory, func), Generator<FileSpec.Builder, File>
{
    constructor(project: GradleProject, func: () -> Unit) : this(project.dir.toFile(), func)

    final override val extension get() = "kts"

    override fun createModel() = FileSpec.scriptBuilder(name)

    override val FileSpec.Builder.log get() = name

    final override fun FileSpec.Builder.write(target: File) = build().writeTo(directory)
}
