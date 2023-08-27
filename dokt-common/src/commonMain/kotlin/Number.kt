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
