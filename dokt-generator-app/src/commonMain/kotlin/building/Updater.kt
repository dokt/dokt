package app.dokt.generator.building

import kotlin.system.measureTimeMillis

interface Updater<S, M : Any, T> : Writer<M, T>, Reader<S, M> {
    fun update(): Boolean {
        val previous = read()
        val current: M?
        if (previous == null) debug { "Creating new." }
        else debug { "Updating ${previous.log}." }
        val ms = measureTimeMillis {
            current = update(previous)
        }
        // Model equals should work. Works with String, List and Map.
        return if (current == null || current == previous) {
            info { "Up-to-date in $ms ms." }
            false
        } else {
            info { "Updated to ${current.log} in $ms ms." }
            current.write()
            true
        }
    }

    fun update(previous: M?): M?
}