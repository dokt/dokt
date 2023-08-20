package app.dokt.generator.building

import app.dokt.generator.code.*
import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Path

abstract class FileWriter {
    abstract val directory: Path

    abstract val extension: String

    open val file by lazy { directory.resolve("$name.$extension").toFile()!! }

    abstract val name: String

    abstract fun write()
}

abstract class KotlinScriptWriter : FileWriter() {
    final override val extension = "kts"

    val kotlinFile by lazy { if (file.exists()) KotlinFile(file) else null }

    fun generateScript() = script(name) { generateScript() }

    override fun write() = generateScript().writeTo(directory)

    protected abstract fun FileSpec.Builder.generateScript()
}

abstract class PropertiesUpdater : FileWriter(), Updater {
    final override val extension = "properties"

    protected val map = mutableMapOf<String, String>().toSortedMap()

    private fun read() {
        if (file.exists()) file.readLines().forEach { line ->
            if (line.isNotBlank() && !line.startsWith('#')) {
                line.split("=").let { (key, value) -> map[key.trim()] = value.trim() }
            }
        }
    }

    final override fun update() {
        read()
        updateProperties()
        write()
    }

    protected open fun updateProperties() {}

    final override fun write() = file.writeText(map.map { (key, value) -> "$key=$value" }.joinToString("\n") )
}