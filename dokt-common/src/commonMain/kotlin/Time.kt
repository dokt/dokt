package app.dokt.common

import kotlinx.datetime.*

/** The default clock instance, which may be changed on tests */
var clock: Clock = Clock.System

val helsinki = TimeZone.of("Europe/Helsinki")

/**
 * Epoch seconds. Seconds since Thursday 1 January 1970 00:00:00 UTC.
 */
typealias Epoch = UInt

expect fun epoch(): Epoch

val Instant.helsinkiTime get() = toLocalDateTime(helsinki).time.toString().substringBefore('.')

/** Short valid [format](https://en.wikipedia.org/wiki/ISO_8601) including milliseconds */
val Instant.short get() = toString().filter { it != '-' && it != ':' }

/** Shortest valid [format](https://en.wikipedia.org/wiki/ISO_8601) excluding milliseconds */
val Instant.shortest get() = "${short.substringBefore('.')}Z"

val Instant.since get() = clock.now() - this