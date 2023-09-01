package app.dokt.generator.code.kotlinpoet

import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Path
import kotlin.io.path.writer
import kotlin.system.measureTimeMillis

open class ScriptGenerator(fileName: String, func: () -> Unit) :
    FileGenerator(FileSpec.scriptBuilder("$fileName.kts"), func)
{
    final override fun writeTo(dir: Path, commonRoot: String) {
        val path = dir.resolve("${fileSpec.name}.kt")
        val ms = measureTimeMillis {
            Sanitizer(path.writer()).use { fileSpec.writeTo(it) }
        }
        debug { "'$path' was written and sanitized in $ms ms." }
    }
}
