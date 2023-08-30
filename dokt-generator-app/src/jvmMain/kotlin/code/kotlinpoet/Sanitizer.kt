package app.dokt.generator.code.kotlinpoet

import app.dokt.common.remove
import java.io.Closeable
import java.io.Writer

private const val IMPORT_KOTLIN = "import kotlin."

/**
 * Removes following from KotlinPoet output:
 * - Unnecessary Kotlin core imports
 * - Redundant public modifiers
 * - Redundant Unit return types
 */
class Sanitizer(private val out: Writer) : Appendable, Closeable {
    private val builder = StringBuilder()
    private var wrotePreviousLine = true

    override fun append(csq: CharSequence): Appendable {
        csq.forEach { append(it) }
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int) = append(csq.subSequence(start, end))

    override fun append(c: Char): Appendable {
        builder.append(c)
        if (c == '\n') {
            val line = builder.toString()
            // Ignore kotlin.* but allow e.g. kotlin.jvm.Inline
            if (line.startsWith(IMPORT_KOTLIN) && line[IMPORT_KOTLIN.length].isUpperCase()) wrotePreviousLine = false
            else if (wrotePreviousLine || line.isNotBlank()) {
                // Omitting `public` modifier and `Unit` return.
                out.write(line.remove("public ", ": Unit"))
                wrotePreviousLine = true
            }
            builder.clear()
        }
        return this
    }

    override fun close() = out.close()
}
