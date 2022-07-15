@file:Suppress("unused")

package app.dokt

// https://en.wikipedia.org/wiki/ANSI_escape_code
// https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

/**
 * Escape, 27, 0x1B, 033
 */
private const val Esc = '\u001B'

/**
 * Control Sequence Introducer
 */
private const val Csi = "$Esc["

const val ForeBlack = "${Csi}30m"
const val ForeRed = "${Csi}31m"
const val ForeGreen = "${Csi}32m"
const val ForeYellow = "${Csi}33m"
const val ForeBlue = "${Csi}34m"
const val ForePurple = "${Csi}35m"
const val ForeCyan = "${Csi}36m"
const val ForeWhite = "${Csi}37m"

const val BackBlack = "${Csi}40m"
const val BackRed = "${Csi}41m"
const val BackGreen = "${Csi}42m"
const val BackYellow = "${Csi}43m"
const val BackBlue = "${Csi}44m"
const val BackPurple = "${Csi}45m"
const val BackCyan = "${Csi}46m"
const val BackWhite = "${Csi}47m"

private const val Reset = "${Csi}m"

fun ansi(builder: StringBuilder.() -> Unit) = StringBuilder().apply {
    builder()
}.reset.toString()

val Appendable.reset get() = append(Reset)!!

interface Ansible {
    val ansi: String
}
