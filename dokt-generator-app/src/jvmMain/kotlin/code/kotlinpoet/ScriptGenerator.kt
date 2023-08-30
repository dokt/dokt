package app.dokt.generator.code.kotlinpoet

import com.squareup.kotlinpoet.FileSpec

open class ScriptGenerator(fileName: String, func: () -> Unit) :
    FileGenerator(FileSpec.scriptBuilder("$fileName.kts"), func)
