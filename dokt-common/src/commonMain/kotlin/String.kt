@file:Suppress("unused")

package app.dokt.common

private val WHITESPACE_REGEX = "\\s".toRegex()

fun <T> Iterable<T>.joinWithSpace(transform: ((T) -> CharSequence)? = null) = joinToString(" ", transform = transform)

val String.capitalize get() = lowercase().uppercaseFirst

val String.first get() = first()

val String.second get() = this[1]

val String.secondInt get() = second.toString().toInt()

val String.third get() = this[2]

/**
 * List whitespace separated tokens. Skips blank tokens.
 * TODO Skipping can be done using req-ex: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 */
val String.tokens get() = split(WHITESPACE_REGEX).filter { it.isNotBlank() }

val String.uppercaseFirst get() = replaceFirstChar { it.uppercaseChar() }

fun <R> String.tokenized(apply: List<String>.() -> R) = apply(tokens)
