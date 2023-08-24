package app.dokt.generator.building

import app.dokt.infra.Log
import kotlin.system.measureNanoTime

interface Writer<M : Any, T>: Log {
    val target: T

    val M.log: String

    fun M.write() {
        debug { "Writing $log to $target." }
        val ns = measureNanoTime {
            write(target)
        }
        info { "Written $log c in $ns ns to $target." }
    }

    fun M.write(target: T)
}
