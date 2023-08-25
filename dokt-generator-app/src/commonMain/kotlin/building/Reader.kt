package app.dokt.generator.building

import app.dokt.infra.Log
import kotlin.system.measureTimeMillis

interface Reader<S, M : Any> : Log {
    val source: S

    fun read(): M? {
        debug { "Reading $source." }
        val model: M?
        val ms = measureTimeMillis {
            model = source.readModel()
        }
        if (model == null) warn { "$source not found in $ms!" }
        else info { "Read ${model.log} in $ms ns from $source." }
        return model
    }

    val M.log: String

    fun S.readModel(): M?
}