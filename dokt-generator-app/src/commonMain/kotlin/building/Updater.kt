package app.dokt.generator.building

import kotlin.system.measureNanoTime

interface Updater<S, M : Any, T> : Writer<M, T>, Reader<S, M> {
    fun update() {
        val previous = read()
        val current: M?
        if (previous == null) debug { "Creating new." }
        else debug { "Updating ${previous.log}." }
        val ns = measureNanoTime {
            current = update(previous)
        }
        // Model equals should work. Works with String, List and Map.
        if (current == null || current == previous) info { "Up-to-date in $ns ns." }
        else {
            info { "Updated to ${current.log} in $ns ns." }
            current.write()
        }
    }

    fun update(previous: M?): M?
}