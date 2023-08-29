// TODO replace with multiplatform
@file:Suppress("unused")

package app.dokt.common

import java.time.Duration
import java.time.Instant

private const val MILLIS_IN_SECOND = 1_000

actual fun epoch() = (System.currentTimeMillis() / MILLIS_IN_SECOND).toUInt()

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
