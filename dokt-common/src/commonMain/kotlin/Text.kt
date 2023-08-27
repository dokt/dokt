@file:Suppress("unused")
/** Common english extensions */
package app.dokt.common

import kotlin.math.pow
import kotlin.math.roundToLong

private const val B = 1024L
private const val GIGABYTE = 1073741824.0

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
    ((this / GIGABYTE) * it).roundToLong() / it
}

fun String.pluralize(collection: Collection<Any?> = emptyList()) =
    if (collection.isEmpty()) pluralize(0)
    else "${pluralize(collection.size)} (${collection.joinToString()})"

fun String.pluralize(count: Int = 1) = when {
    count == 1 -> "1 $this"
    endsWith('s') -> "$count ${this}es"
    else -> "$count ${this}s"
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
        val String.toText get() = Text(this).also { require(isNotBlank()) { "Text can't be blank!" } }
    }
}
