@file:Suppress("unused")

package app.dokt.common

// https://en.wikipedia.org/wiki/ANSI_escape_code
// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

/** Escape, 27, 0x1B, 033 */
private const val ESC = '\u001B'

/** Control Sequence Introducer */
private const val CSI = "$ESC["

const val FORE_BLACK = "${CSI}30m"
const val FORE_RED = "${CSI}31m"
const val FORE_GREEN = "${CSI}32m"
const val FORE_YELLOW = "${CSI}33m"
const val FORE_BLUE = "${CSI}34m"
const val FORE_PURPLE = "${CSI}35m"
const val FORE_CYAN = "${CSI}36m"
const val FORE_WHITE = "${CSI}37m"

const val BACK_BLACK = "${CSI}40m"
const val BACK_RED = "${CSI}41m"
const val BACK_GREEN = "${CSI}42m"
const val BACK_YELLOW = "${CSI}43m"
const val BACK_BLUE = "${CSI}44m"
const val BACK_PURPLE = "${CSI}45m"
const val BACK_CYAN = "${CSI}46m"
const val BACK_WHITE = "${CSI}47m"

private const val RESET = "${CSI}m"

fun ansi(builder: StringBuilder.() -> Unit) = StringBuilder().apply {
    builder()
}.reset.toString()

val Appendable.reset get() = append(RESET)!!

interface Ansible {
    val ansi: String
}
