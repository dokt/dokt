package app.dokt.generator.building

import app.dokt.infra.Log
import kotlin.system.measureNanoTime

interface Reader<S, M : Any> : Log {
    val source: S

    fun read(): M? {
        debug { "Reading $source." }
        val model: M?
        val ns = measureNanoTime {
            model = source.readModel()
        }
        if (model == null) warn { "$source not found in $ns!" }
        else info { "Read ${model.log} in $ns ns from $source." }
        return model
    }

    val M.log: String

    fun S.readModel(): M?
}