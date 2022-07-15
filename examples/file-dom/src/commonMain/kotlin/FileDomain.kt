package fi.papinkivi.file

import app.dokt.domain.Root
import fi.papinkivi.hash.Hash
import kotlinx.serialization.Serializable

class Corrupted : Exception()

interface Events {
    fun created(checksum: String, size: Int)
}

@Serializable
class FileInfo(val id: String) : Root<Events>(), Events {
    /** The base name of the file */
    val baseName get() = filename.baseName

    /** MD5 hash */
    lateinit var checksum: String
        private set

    /** Suffix of the filename which defines file type */
    val extension get() = filename.extension

    /** The full name of the file */
    val filename get() = id.filename

    /** The path to directory where file is located */
    val path get() = id.path

    /** File size in bytes */
    var size = 0
        private set

    fun check(data: ByteArray) = hash(data, checksum)

    fun create(data: ByteArray, checksum: String? = null) = emit.created(hash(data, checksum), data.size)

    override fun created(checksum: String, size: Int) {
        this.checksum = checksum
        this.size = size
    }

    companion object {
        val String.baseName get() = substringBefore('.')

        val String.extension get() = if (contains('.')) substringAfter('.') else null

        val String.filename get() = substringAfterLast('/')

        val String.path get() = substringBeforeLast('/')

        fun hash(data: ByteArray, checksum: String? = null) = Hash.MD5.hash(data).apply {
            checksum?.let { if (this != it) throw Corrupted() }
        }
    }
}
