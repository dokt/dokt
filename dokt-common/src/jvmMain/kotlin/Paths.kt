@file:Suppress("unused")

package app.dokt

import java.io.File
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

val File.empty get() = length() == 0L

fun File.deleteIfEmpty() { if (empty) delete() }

fun Path.filterDirectoryEntries(glob: String = "*", filter: (Path) -> Boolean) = listDirectoryEntries(glob).filter(filter)

fun Path.listDirectories() = try {
    filterDirectoryEntries { it.isDirectory() }
} catch (e: Throwable) {
    emptyList()
}