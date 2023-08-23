package app.dokt.generator.building

import app.dokt.infra.Logger
import java.io.File

abstract class AbstractFileUpdater(override val file: File, func: () -> Unit) : Logger(func), FileUpdater {
    override val modified get() = file.lastModified()
}