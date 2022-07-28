package fi.papinkivi.file

import kotlin.io.path.*

/** Local file system files implementation */
object LocalFiles : Files {
    var path = Path("files")

    override operator fun get(id: FileId) = path.resolve(id.path).readBytes()

    override operator fun set(id: FileId, data: ByteArray) = path.resolve(id.path).writeBytes(data)
}