package app.dokt.common

typealias CommentsToValue = Pair<List<String>, String?>
typealias Props = Map<String, CommentsToValue>

fun Props.lines(delimiter: String = " = ") = flatMap { (key, prop) ->
    val escapedKey = key
        .replace(":", "\\:")
        .replace("=", "\\=")
        .replace(" ", "\\ ")
    val (comments, value) = prop
    comments + (value?.let {
        "$escapedKey$delimiter${it
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        }"
    } ?: escapedKey)
}

/** [Properties](https://en.wikipedia.org/wiki/.properties) store the configurable parameters of an application. */
class PropertyReader(lines: Sequence<String>) {
    private val lines = lines.iterator()
    private val lineNo get() = currentLineIndex + 1
    private var currentLine: String? = null
    private var currentLineIndex = -1
    private var currentCharIndex = 0
    private val currentChar get() = currentLine
        ?.let { if (currentCharIndex < it.length) it[currentCharIndex] else null }
    private val builder = StringBuilder()
    private val charNo get() = currentCharIndex + 1
    private val location get() = "$lineNo:$charNo"
    private var missingNextValue = false

    private fun nextLine(): String? {
        currentLine = if (lines.hasNext()) {
            currentLineIndex++
            lines.next()
        } else null
        currentCharIndex = 0
        return currentLine
    }

    fun properties(): Props {
        val properties = mutableMapOf<String, CommentsToValue>()
        do {
            val property = property()
            property?.let { (key, newProp) ->
                properties[key] = properties[key]?.let { (oldComments, _) ->
                    val (newComments, value) = newProp
                    oldComments + newComments to value
                } ?: newProp
            }
        } while (property != null)
        return properties
    }

    fun property() : Pair<String, CommentsToValue>? {
        val comments = mutableListOf<String>()
        do {
            val comment = comment()
            comment?.let { comments.add(it) }
        } while (comment != null)
        skipLineWhitespace()
        if (currentChar == null) return null
        builder.clear()
        missingNextValue = false
        return key() to (comments to value())
    }

    private fun comment() : String? {
        val line = nextLine() ?: return null
        skipLineWhitespace()
        return when (currentChar) {
            '#', '!' -> line
            else -> null
        }
    }

    private fun key(): String {
        val line = currentLine!!
        var delimiter: Char? = null
        while (delimiter == null && currentCharIndex < line.length) {
            when (val char = line[currentCharIndex]) {
                '\\' -> {
                    currentCharIndex++
                    when (val escaped = currentChar) {
                        null -> throw IllegalStateException(
                            "Missing property key escape character on $location:\n$currentLine")
                        '=', ':', ' ' -> {
                            builder.append(escaped)
                            currentCharIndex++
                        }
                        else -> throw IllegalStateException(
                            "Invalid property key escape character on $location:\n$currentLine")
                    }
                }
                ' ', '=', ':', '\t', '\u000c' -> {
                    delimiter = char
                    currentCharIndex++
                    while (currentCharIndex < line.length) {
                        when (line[currentCharIndex]) {
                            ' ', '=', ':', '\t', '\u000c' -> currentCharIndex++
                            else -> break
                        }
                    }
                }
                else -> {
                    builder.append(char)
                    currentCharIndex++
                }
            }
        }
        if (delimiter == null) missingNextValue = true
        return builder.toString().also { builder.clear() }
    }

    private fun skipLineWhitespace() {
        val line = currentLine ?: return
        while (currentCharIndex < line.length && line[currentCharIndex].isWhitespace()) currentCharIndex++
    }

    private fun value(): String? {
        if (missingNextValue) return null
        var line = currentLine
        while (line != null && currentCharIndex < line.length) {
            when (val char = line[currentCharIndex]) {
                '\\' -> {
                    currentCharIndex++
                    when (val escaped = currentChar) {
                        null -> {
                            line = nextLine()
                            skipLineWhitespace()
                        }
                        'n' -> {
                            builder.append('\n')
                            currentCharIndex++
                        }
                        'r' -> {
                            builder.append('\r')
                            currentCharIndex++
                        }
                        't' -> {
                            builder.append('\t')
                            currentCharIndex++
                        }
                        'u' -> {
                            try {
                                currentCharIndex += 5
                                builder.append(line.substring(currentCharIndex - 4, currentCharIndex).unicode)
                            } catch (e: Exception) {
                                throw IllegalStateException("Invalid Unicode on $location:\n$currentLine", e)
                            }
                        }
                        else -> {
                            builder.append(escaped)
                            currentCharIndex++
                        }
                    }
                }
                else -> {
                    builder.append(char)
                    currentCharIndex++
                }
            }
        }
        return builder.toString()
    }
}
