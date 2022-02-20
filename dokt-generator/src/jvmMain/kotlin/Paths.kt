package app.dokt.generator

import java.io.File
import java.nio.file.Path
import kotlin.io.path.*

fun Path.listDirectories() = try { listDirectoryEntries().filter { it.isDirectory() }
} catch (e: Throwable) {
    emptyList()
}

fun String.moduleToPath() = replace('.', File.separatorChar)
