package app.dokt.generator.building

import java.io.File

interface FileUpdater : Updater {
    val file: File
    val modified: Long
}