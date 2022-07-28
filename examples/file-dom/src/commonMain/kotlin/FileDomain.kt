@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package fi.papinkivi.file

import app.dokt.domain.Root
import fi.papinkivi.hash.Hash
import kotlinx.serialization.*

class Corrupted : Exception()

interface Events {
    fun created(checksum: String, size: Int)
}

@Serializable
class FileInfo(val id: FileId) : Root<Events>(), Events {
    /** The base name of the file */
    val baseName get() = id.baseName

    /** MD5 hash */
    lateinit var checksum: String
        private set

    @Transient
    private lateinit var _data: ByteArray
    val data: ByteArray get() {
        if (!this::_data.isInitialized) _data = files[id]
        return _data
    }

    /** Suffix of the filename which defines file type */
    val extension get() = id.extension

    /** The full name of the file */
    val filename get() = id.name

    /** The path to directory where file is located */
    val path get() = id.path

    /** File size in bytes */
    var size = 0
        private set

    fun check() = hash(data, checksum)

    fun create(data: ByteArray, checksum: String? = null) {
        val hash = hash(data, checksum)
        files[id] = data
        emit.created(hash, data.size)
    }

    override fun created(checksum: String, size: Int) {
        this.checksum = checksum
        this.size = size
    }

    companion object {
        lateinit var files: Files

        fun hash(data: ByteArray, checksum: String? = null) = Hash.MD5.hash(data).apply {
            checksum?.let { if (this != it) throw Corrupted() }
        }
    }
}

@JvmInline
@Serializable
value class FileId(val path: String) {
    /** The base name of the file */
    val baseName get() = name.substringBefore('.')

    /** The path to directory where file is located */
    val dir get() = path.substringBeforeLast('/')

    /** Suffix of the filename which defines file type */
    val extension get() = name.let { if (it.contains('.')) it.substringAfter('.') else null }

    /** The full name of the file */
    val name get() = path.substringAfterLast('/')

    override fun toString() = path
}

/** File store service */
interface Files {
    /** Read file data from store */
    operator fun get(id: FileId): ByteArray

    /** Write file data to store */
    operator fun set(id: FileId, data: ByteArray)
}