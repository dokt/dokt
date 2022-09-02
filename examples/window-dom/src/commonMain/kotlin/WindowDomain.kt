package fi.papinkivi.window

import app.dokt.common.*
import app.dokt.domain.Root
import fi.papinkivi.file.FileId
import fi.papinkivi.file.FileInfo
import kotlinx.serialization.Serializable

@Serializable
data class WindowId(val processPath: String, val title: String = "")

interface Events {
    fun captured(file: FileId)
    fun hidden()
    fun moved(point: Pt)
    fun resized(dimension: Dim)
}

@Serializable
class Window(private val id: WindowId) : Root<Events>(), Events {
    var location = Pt.ZERO
        private set

    var area = Rect.ZERO
        private set

    var size = Dim.ZERO
        private set

    var visible = false
        private set

    val screenshots = mutableListOf<FileId>()

    //#region Command handlers
    fun capture() {
        if (!visible) return
        val fileId = FileId.uuid(screen.captureExtension)
        val file = FileInfo(fileId)
        file.create(screen.capture(area)) // TODO use service here
        emit.captured(fileId)
    }

    fun measure() {
        val rect = screen.measure(id)
        if (rect == null) emit.hidden()
        else {
            rect.location.let { if (it != location) moved(it) }
            rect.size.let { if (it != size) resized(it) }
        }
    }
    //#endregion

    //#region Event handlers
    override fun captured(file: FileId) { screenshots.add(file) }

    override fun hidden() { visible = false }

    override fun moved(point: Pt) {
        visible = true
        location = point
        area = Rect(location, size)
    }

    override fun resized(dimension: Dim) {
        visible = true
        size = dimension
        area = Rect(location, size)
    }
    //#endregion

    companion object {
        lateinit var screen: Screen
    }
}

interface Screen {
    val captureExtension: String

    /** Take screenshot */
    fun capture(area: Rect): ByteArray

    /** Measure location and size */
    fun measure(id: WindowId): Rect?
}