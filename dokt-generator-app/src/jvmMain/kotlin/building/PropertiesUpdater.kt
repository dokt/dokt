package app.dokt.generator.building

import app.dokt.common.PropertyReader
import app.dokt.common.Props
import app.dokt.common.lines
import app.dokt.common.writeLines
import java.io.File

abstract class PropertiesUpdater(dir: File, func: () -> Unit) :
    FileUpdater<Props>(dir, func)
{
    final override val extension get() = "properties"

    override fun File.readModel() =
        if (exists()) useLines { PropertyReader(it).properties().toSortedMap() }
        else null

    override val Props.log get() = "$size properties"

    override fun Props.write(target: File) {
        target.writeLines(lines())
    }
}
