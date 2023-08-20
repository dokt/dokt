package app.dokt.generator

import java.io.File
import java.nio.file.Path
import kotlin.io.path.*

fun String.moduleToPath() = replace('.', File.separatorChar)
