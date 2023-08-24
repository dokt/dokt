package app.dokt.generator.building

import app.dokt.common.writeLines
import java.io.File
import java.util.*

abstract class PropertiesUpdater(directory: File, func: () -> Unit) :
    FileUpdater<SortedMap<String, String>>(directory, func) {
    final override val extension = "properties"

    override fun File.readModel() =
        if (exists()) readLines().filter { it.isNotBlank() && !it.startsWith('#') }.associate { line ->
            line.split("=").let { (key, value) -> key.trim() to value.trim() }
        }.toSortedMap()
        else null

    override val SortedMap<String, String>.log get() = "$size properties"

    override fun SortedMap<String, String>.write(target: File) {
        target.writeLines(lines)
    }

    val Map<String, String>.lines get() = map { it.line }

    val Map.Entry<String, String>.line get() = "$key=$value"

    val Map<String, String>.text get() = lines.joinToString("\n")
}