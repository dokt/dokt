@file:Suppress("unused")

package app.dokt.common

/**
 * https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
 */
private val CAMEL_CASE_REGEX = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])".toRegex()

private val WHITESPACE_REGEX = "\\s".toRegex()

fun Array<String>.prefix(prefix: String) = map { prefix + it }

fun Array<String>.suffix(suffix: String) = map { it + suffix }

fun <T> Iterable<T>.joinWithSpace(transform: ((T) -> CharSequence)? = null) = joinToString(" ", transform = transform)

val Iterable<String>.commonPrefix get() = iterator().run {
    if (hasNext()) {
        var prefix = next()
        while (hasNext()) prefix = prefix.commonPrefixWith(next())
        prefix
    } else ""
}

fun Iterable<String>.prefix(prefix: String) = map { prefix + it }

fun Iterable<String>.suffix(suffix: String) = map { it + suffix }

fun List<String>.anyContains(search: String) = any { it.contains(search) }

fun List<String>.joinLines(separator: String = "\n") = joinToString(separator)

/**
 * Adds line after the line which contains the search string.
 *
 * @return Index of the found line or `-1`.
 */
fun MutableList<String>.addAfterContains(search: String, lineToAdd: String) = indexOfFirst { it.contains(search) }
    .also {
        if (it > -1) add(it + 1, lineToAdd)
    }

val String.camelCaseToWords get() = splitCamelCase.joinToString(" ")

val String.capitalize get() = lowercase().upperFirst

val String.first get() = first()

val String.lowerFirst get() = replaceFirstChar { it.lowercaseChar() }

val String.second get() = this[1]

val String.secondInt get() = second.toString().toInt()

val String.splitCamelCase get() = split(CAMEL_CASE_REGEX)

val String.third get() = this[2]

/**
 * List whitespace separated tokens. Skips blank tokens.
 * TODO Skipping can be done using req-ex: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
 */
val String.tokens get() = split(WHITESPACE_REGEX).filter { it.isNotBlank() }

val String.upperFirst get() = replaceFirstChar { it.uppercaseChar() }

/** Converts unicode hex notation \u<hex> to character. E.g. 306f = は */
@OptIn(ExperimentalStdlibApi::class)
val String.unicode get() = Char(hexToInt())

fun <R> String.tokenized(apply: List<String>.() -> R) = apply(tokens)
