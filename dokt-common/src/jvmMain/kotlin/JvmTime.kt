// TODO replace with multiplatform
@file:Suppress("unused")

package app.dokt

import java.time.*

actual fun epoch() = (System.currentTimeMillis() / 1000).toUInt()

fun duration(operation: () -> Unit): Duration {
    val begin = Instant.now()
    operation()
    val end = Instant.now()
    return Duration.between(begin, end)
}

fun nowToTinyIsoDateTime() = Instant.now().toString()
    .substringBefore('.')
    .replace("-", "")
    .replace(":", "")
