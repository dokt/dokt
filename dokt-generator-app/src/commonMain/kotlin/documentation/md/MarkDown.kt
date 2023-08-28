@file:Suppress("MagicNumber")

package app.dokt.generator.documentation.md

import app.dokt.generator.documentation.Align
import app.dokt.generator.documentation.Documentation
import app.dokt.generator.documentation.Table

/**
 * https://www.markdownguide.org/basic-syntax/
 * https://www.markdownguide.org/extended-syntax/
 * // TODO Use existing library e.g.: https://github.com/kokorins/markdown-generator
 */
class MarkDownDocumentation(private val out: Appendable) : Documentation {
    var level = 1
        private set

    override fun bold(text: Any) = "**${text}**"

    override fun decrementLevel(): Documentation {
        if (level > 1) level--
        return this
    }

    override fun definition(term: Any, definition: Any): Documentation {
        out.appendLine(escape(term)).appendLine(": ${escape(definition)}").appendLine()
        return this
    }

    private fun escape(any: Any) = any.toString().replace("<", "\\<")

    override fun heading(phrase: Any) = heading(phrase, level)

    override fun heading(phrase: Any, level: Int) : Documentation {
        val text = escape(phrase)
        when (level) {
            1 -> {
                out.appendLine(text)
                repeat(text.length) {
                    out.append('=')
                }
                out.appendLine()
            }
            2 -> {
                out.appendLine(text)
                repeat(text.length) {
                    out.append('-')
                }
                out.appendLine()
            }
            in 3..6 -> {
                repeat(level) {
                    out.append('#')
                }
                out.appendLine(" $text")
            }
            else -> throw IllegalArgumentException("$level isn't valid heading level!")
        }
        out.appendLine()
        return this
    }

    override fun horizontalRule(): Documentation {
        out.appendLine("___________________________________________________________________________").appendLine()
        return this
    }

    override fun incrementLevel(): Documentation {
        if (level < 6) level++
        return this
    }

    override fun italic(text: Any) = "*$text*"

    override fun ordered(list: List<Any>): Documentation {
        list.forEachIndexed { index, item ->
            out.appendLine("${index + 1}. ${escape(item)}")
        }
        out.appendLine()
        return this
    }

    override fun paragraph(lines: Any): Documentation {
        out.appendLine(escape(lines)).appendLine()
        return this
    }

    override fun table() = MarkDownTable(out)
}

class MarkDownTable(private val out: Appendable) : Table {
    override fun build() {
        TODO("Not yet implemented")
    }

    override fun column(header: Any, align: Align): Table {
        TODO("Not yet implemented")
    }

    override fun row(vararg texts: Any): Table {
        TODO("Not yet implemented")
    }
}
