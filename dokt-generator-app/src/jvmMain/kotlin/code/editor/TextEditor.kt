package app.dokt.generator.code.editor

import app.dokt.common.intersects
import app.dokt.common.requireNull
import app.dokt.infra.Logger

/** Text editor which modifies only original text. */
class TextEditor(text: String) : Logger({}) {
    private val buffer = StringBuffer(text)

    private val changes = mutableListOf<IntRange>()

    /** Deltas by start index. */
    private val deltas = mutableMapOf<Int, Int>()

    private val length = text.length

    init {
        trace { "Original text is: $text" }
    }

    /**
     * Adds text to a specific index.
     * @param index Index where start adding the text.
     * @param addition The text to add.
     */
    fun add(index: Int, addition: String) {
        debug { "Adding '$addition' to $index index." }
        if (addition.isEmpty()) {
            info { "Ignoring empty addition to $index index!" }
            return
        }
        if (index <= 0) {
            if (index < 0) warn { "Add index $index should not be negative!" }
            return prepend(addition)
        }
        if (index >= length) {
            if (index > length) warn { "Add index $index should not be greater than text length $length!" }
            return append(addition)
        }
        requireNull(changes.find { index in it }) {
            "Can't add '$addition' to index $index because $it range already changed!" }
        val position = index + countDelta(index)
        buffer.insert(position, addition)
        info { "Added '$addition' to position $position." }
        saveDelta(index, addition.length)
    }

    /**
     * Appends text to the end.
     * @param text The text to append.
     */
    fun append(text: String) {
        debug { "Appending '$text' to the end index $length." }
        if (text.isEmpty()) {
            info { "Ignoring empty append!" }
            return
        }
        buffer.append(text)
        info { "Appended '$text' to the end index $length." }
    }

    private fun countDelta(index: Int): Int {
        trace { "Counting delta for $index index." }
        val delta = deltas.filterKeys {
            if (it <= index) {
                debug { "Including ${deltas[it]} delta at $it index." }
                true
            } else {
                trace { "Excluding ${deltas[it]} delta at $it index." }
                false
            }
        }.values.sum()
        debug { "Total delta is $delta for $index index." }
        return delta
    }

    private fun countSelection(range: IntRange) = countDelta(range.last).let {
        var start = range.first + it // Inclusive
        if (start < 0) {
            warn { "Selection start $start shouldn't be negative!" }
            start = 0
        }
        var end = range.last + it + 1 // Exclusive.
        if (end > length) {
            warn { "Selection end $end shouldn't be greater than length $length!" }
            end = length
        }
        start to end
    }

    /**
     * Deletes text.
     * @param range The index range where to delete.
     * @return The deleted text.
     */
    fun delete(range: IntRange): String {
        debug { "Deleting $range range." }
        if (range.isEmpty()) {
            info { "Nothing to delete on $range empty range!" }
            return ""
        }
        requireNull(changes.find { it.intersects(range) }) {
            "Can't delete $range range because $it range was already changed!" }
        val (start, end) = countSelection(range)
        val deleted = buffer.substring(start, end)
        buffer.delete(start, end)
        info { "Deleted '$deleted' from position $start..<$end." }
        saveChange(range, -range.count())
        return deleted
    }

    /**
     * Prepends text before the start.
     * @param text The text to prepend.
     */
    fun prepend(text: String) {
        debug { "Prepending '$text' before the start." }
        if (text.isEmpty()) {
            info { "Ignoring empty prepend!" }
            return
        }
        buffer.insert(0, text)
        info { "Prepended '$text' before the start." }
        saveDelta(0, text.length)
    }

    /**
     * Replaces some range with text.
     * @param range The index range what to replace.
     * @param text The replacement text.
     * @return The replaced text.
     */
    fun replace(range: IntRange, text: String) : String {
        debug { "Replacing $range range with '$text'." }
        if (range.isEmpty()) {
            info { "Use addition for $range empty range!" }
            add(range.first, text)
            return ""
        }
        if (text.isEmpty()) {
            info { "Use deletion for empty text!" }
            return delete(range)
        }
        requireNull(changes.find { it.intersects(range) }) {
            "Can't replace $range range to value '$text' because $it range has been previously changed!" }
        val (start, end) = countSelection(range)
        val replaced = buffer.substring(start, end)
        buffer.replace(start, end, text)
        info { "Replaced '$replaced' in position $start..<$end with '$text'." }
        saveChange(range, text.length - range.count())
        return replaced
    }

    private fun saveChange(range: IntRange, delta: Int) {
        trace { "Saving $range range with $delta delta." }
        if (!range.isEmpty()) {
            debug { "Adding $range range to changes." }
            changes.add(range)
        } else {
            warn { "Skipping $range empty range." }
        }
        saveDelta(range.first, delta)
    }

    private fun saveDelta(index: Int, delta: Int) {
        trace { "Saving $delta delta to $index index." }
        if (delta == 0) {
            debug { "Ignoring 0 delta." }
        } else {
            val oldDelta = deltas[index]
            if (oldDelta == null) {
                debug { "Adding new $delta delta to $index index." }
                deltas[index] = delta
            } else {
                val newDelta = oldDelta + delta
                debug { "Updating delta from $oldDelta to $newDelta on index $index." }
                deltas[index] = newDelta
            }
        }
        trace { "Text is now: $this" }
    }

    override fun toString() = buffer.toString()
}
