@file:Suppress("unused")

package app.dokt.common

import java.io.File
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.writeLines

val File.empty get() = length() == 0L

fun File.deleteIfEmpty() { if (empty) delete() }

fun File.writeLines(lines: Iterable<CharSequence>, charset: Charset = Charsets.UTF_8, vararg options: OpenOption) =
    toPath().writeLines(lines, charset, *options)

fun Path.filterDirectoryEntries(glob: String = "*", filter: (Path) -> Boolean) = listDirectoryEntries(glob).filter(filter)

fun Path.listDirectories() = try {
    filterDirectoryEntries { it.isDirectory() }
} catch (e: Throwable) {
    emptyList()
}