package app.dokt.common

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

private const val TWO_DECIMALS = 100f

val Double.roundAwayFromZero get() = if (this < 0) floor(this).toInt() else ceil(this).toInt()

/** Round to 2 decimals */
val Float.round2 get() = round(this * TWO_DECIMALS) / TWO_DECIMALS

val Float.kotlin get(): String {
    val string = toString()
    var index = string.length
    var char: Char
    do { char = string[--index] } while (char == '0')
    if (char == '.') index--
    return "${string.substring(0..index)}f"
}

val <T : Comparable<T>> ClosedRange<T>.single get() = start == endInclusive

// https://stackoverflow.com/questions/67449343/kotlin-get-values-of-a-range-in-another-range
// TODO add also intersection function which returns range or null.
fun <T : Comparable<T>> OpenEndRange<T>.intersects(other: OpenEndRange<T>) =
    start < other.endExclusive && other.start < endExclusive

