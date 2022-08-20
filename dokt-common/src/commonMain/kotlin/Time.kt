package app.dokt.common

/**
 * Epoch seconds. Seconds since Thursday 1 January 1970 00:00:00 UTC.
 */
typealias Epoch = UInt

expect fun epoch(): Epoch
