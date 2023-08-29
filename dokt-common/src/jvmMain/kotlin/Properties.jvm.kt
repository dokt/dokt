@file:Suppress("unused")

package app.dokt.common

import java.io.File
import java.util.*

fun File.loadProperties() = inputStream().use { Properties().apply { load(it) } }

fun Properties.getInt(key: String) = requireNotNull(getProperty(key)?.toInt()) { "Integer for '$key' key not found!" }

fun Properties.getInt(prefix: String, suffix: String) = getInt("$prefix.$suffix")

fun Properties.setInt(key: String, value: Int) {
    setProperty(key, value.toString())
}

fun Properties.setInt(prefix: String, suffix: String, value: Int) = setInt("$prefix.$suffix", value)

fun Properties.store(file: File, comments: String? = null) = file.outputStream().use { store(it, comments) }
