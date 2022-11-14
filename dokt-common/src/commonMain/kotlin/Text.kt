@file:Suppress("unused")

package app.dokt.common

import kotlin.math.pow
import kotlin.math.roundToLong

private const val B = 1024L

val Long.bytesText : String get() {
    var size = this
    if (size < B) return "$size B"
    size /= B
    if (size < B) return "$size KB"
    size /= B
    if (size < B) return "$size MB"
    size /= B
    if (size < B) return "$size GB"
    size /= B
    return "$size TB"
}

fun Long.gigabytes(fractionDigits: Int = 1) = 10.0.pow(fractionDigits).let {
    ((this / 1073741824.0) * it).roundToLong() / it
}

/** Non-blank text */
@JvmInline
value class Text
    @Deprecated(level = DeprecationLevel.ERROR, message = "use companion methods")
    constructor(private val value: String) {
    override fun toString() = value

    @Suppress("DEPRECATION_ERROR")
    companion object {
        val String?.asText get() = if (isNullOrBlank()) null else Text(this)
        val String.toText get() = if (isBlank()) throwIllegalState() else Text(this)
    }
}
