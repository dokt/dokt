package app.dokt.generator.building

import java.io.File

interface FileHandler<M : Any> : Writer<M, File> {
    val directory: File

    val extension: String

    val filename get() = "$name.$extension"

    val name: String

    fun resolve() = directory.resolve(filename)
}