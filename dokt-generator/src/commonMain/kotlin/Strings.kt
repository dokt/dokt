/**
 * Common english generation extensions
 */
package app.dokt.generator

/**
 * https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
 */
private val camelCaseRegex = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])".toRegex()

val Iterable<String>.commonPrefix get() = iterator().run {
    if (hasNext()) {
        var prefix = next()
        while (hasNext()) prefix = prefix.commonPrefixWith(next())
        prefix
    } else ""
}

fun String.camelCaseToWords() = splitCamelCase().joinToString(" ")

fun String.lowerFirst() = replaceFirstChar { it.lowercase() }

fun String.pluralize(collection: Collection<Any?> = emptyList()) =
    if (collection.isEmpty()) pluralize(0)
    else "${pluralize(collection.size)} (${collection.joinToString()})"

fun String.pluralize(count: Int = 1) = when {
    count == 1 -> "1 $this"
    endsWith('s') -> "$count ${this}es"
    else -> "$count ${this}s"
}

fun String.splitCamelCase() = split(camelCaseRegex)

fun String.upperFirst() = replaceFirstChar { it.uppercase() }
