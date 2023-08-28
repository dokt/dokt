package app.dokt.generator.building

import java.io.File

abstract class FileUpdater<M : Any>(directory: File, func: () -> Unit) :
    FileWriter<M>(directory, func), Updater<File, M, File>
{
    val lastModified get() = source.lastModified()

    final override val source = initTarget()

    private fun initTarget() = target
}
