@file:Suppress("unused")
package app.dokt.common

import java.io.File
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.writeLines

// TODO Use [Path] where possible. See https://www.baeldung.com/java-path-vs-file

val File.empty get() = length() == 0L

fun File.deleteIfEmpty() { if (empty) delete() }

fun File.writeLines(lines: Iterable<CharSequence>, charset: Charset = Charsets.UTF_8, vararg options: OpenOption) =
    toPath().writeLines(lines, charset, *options)

val Path.directories get() = filterEntries { it.isDirectory() }

val Path.lastModifiedMillis get() = getLastModifiedTime().toMillis()

val Path.visibleDirectories get() = filterEntries("[!.]*") { it.isDirectory() && !it.isHidden() }

val Path.visibleDirectoryNames get() = visibleDirectories.map { it.name }

fun Path.filterEntries(glob: String = "*", filter: (Path) -> Boolean) = listDirectoryEntries(glob).filter(filter)
