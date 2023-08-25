package app.dokt.generator.building

import app.dokt.infra.Log
import kotlin.system.measureTimeMillis

interface Writer<M : Any, T>: Log {
    val target: T

    val M.log: String

    fun M.write() {
        debug { "Writing $log to $target." }
        val ms = measureTimeMillis {
            write(target)
        }
        info { "Written $log c in $ms ms to $target." }
    }

    fun M.write(target: T)
}
