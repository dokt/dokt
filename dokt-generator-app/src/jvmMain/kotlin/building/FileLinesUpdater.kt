package app.dokt.generator.building

import app.dokt.common.writeLines
import java.io.File

abstract class FileLinesUpdater(directory: File, func: () -> Unit) : FileUpdater<List<String>>(directory, func) {
    final override fun File.readModel() =
        if (source.exists()) source.readLines()
        else {
            warn { "$source file not found!" }
            null
        }

    final override val List<String>.log get() = "$size lines"

    final override fun List<String>.write(target: File) {
        target.writeLines(this)
    }
}