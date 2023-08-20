@file:Suppress("unused")

package app.dokt.common

import java.io.File
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.*

val File.empty get() = length() == 0L

fun File.deleteIfEmpty() { if (empty) delete() }

fun File.writeLines(lines: Iterable<CharSequence>, charset: Charset = Charsets.UTF_8, vararg options: OpenOption) =
    toPath().writeLines(lines, charset, *options)

val Path.directories get() = try {
    filterEntries { it.isDirectory() }
} catch (e: Throwable) {
    emptyList()
}

val Path.visibleDirectories get() = try {
    filterEntries("[!.]*") { it.isDirectory() && !it.isHidden() }
} catch (e: Throwable) {
    emptyList()
}

val Path.visibleDirectoryNames get() = try {
    visibleDirectories.map { it.name }
} catch (e: Throwable) {
    emptyList()
}

fun Path.filterEntries(glob: String = "*", filter: (Path) -> Boolean) = listDirectoryEntries(glob).filter(filter)
