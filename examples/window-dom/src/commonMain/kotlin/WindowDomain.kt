package fi.papinkivi.window

import app.dokt.common.*
import app.dokt.domain.Root
import fi.papinkivi.file.*
import kotlinx.serialization.Serializable

interface Events {
    fun captured(file: FileId)
    fun detected(processPath: String, icon: ByteArray)
    fun hidden()
    fun moved(point: Pt)
    fun resized(dimension: Dim)
    fun titled(title: String)
}

@Serializable
class Window(private val id: Long) : Root<Events>(), Events {
    var area
        get() = if (visible) Rect(location, size) else null
        private set (value) {
            if (value == null) emit.hidden()
            else {
                value.location.let { if (it != location) emit.moved(it) }
                value.size.let { if (it != size) emit.resized(it) }
            }
        }

    var icon = byteArrayOf()
        private set

    var location = Pt.ZERO
        private set

    var processPath: String = ""
        private set

    val screenshots = mutableListOf<FileId>()

    var size = Dim.ZERO
        private set

    var title = ""
        private set

    var visible = false
        private set

    //#region Command handlers
    fun capture() {
        if (!visible) return
        val fileId = FileId.uuid(screen.captureExtension)
        val file = FileInfo(fileId)
        file.create(screen.capture(area!!)) // TODO use service here
        emit.captured(fileId)
    }

    fun measure() { area = screen.measure(id) }

    fun observe(processPath: String, icon: ByteArray, title: String, area: Rect) {
        detected(processPath, icon)
        if (title.isNotBlank()) titled(title)
        this.area = area
    }
    //#endregion

    //#region Event handlers
    override fun captured(file: FileId) { screenshots.add(file) }

    override fun detected(processPath: String, icon: ByteArray) {
        this.processPath = processPath
        this.icon = icon
    }

    override fun hidden() { visible = false }

    override fun moved(point: Pt) {
        visible = true
        location = point
        area = Rect(location, size)
    }

    override fun titled(title: String) { this.title = title }

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
    fun measure(id: Long): Rect?
}