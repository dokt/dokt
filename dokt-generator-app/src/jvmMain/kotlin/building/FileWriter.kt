package app.dokt.generator.building

import app.dokt.infra.Logger
import java.io.File

abstract class FileWriter<M : Any>(override val directory: File, func: () -> Unit) : Logger(func), FileHandler<M> {

    final override val filename by lazy { "$name.$extension" }

    override val target = resolveFile()

    private fun resolveFile() = resolve()
}
