package app.dokt.generator

import java.io.File

fun String.moduleToPath() = replace('.', File.separatorChar)
