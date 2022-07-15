@file:Suppress("unused")

package app.dokt

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
