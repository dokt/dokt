package fi.papinkivi.window

import app.dokt.common.Pt
import app.dokt.domain.Root
import fi.papinkivi.file.FileId
import kotlinx.serialization.Serializable

@Serializable
data class WindowId(val processPath: String, val title: String = "")

interface Events {
    fun hidden()
    fun moved(point: Pt)
    fun resized(width: Int, height: Int)
    fun screenshotTaken(file: FileId)
}

@Serializable
class Window(private val id: WindowId) : Root<Events>(), Events {
    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var visible = false
    val screenshots = mutableListOf<FileId>()

    //#region Command handlers
    fun detect()

    fun takeScreenshot() { emit.screenshotTaken(screen.takeScreenshot(id)) }
    //#endregion

    //#region Event handlers
    override fun hidden() {
        TODO("Not yet implemented")
    }

    override fun moved(x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun resized(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun screenshotTaken(file: FileId) { screenshots.add(file) }
    //#endregion

    companion object {
        lateinit var screen: Screen
    }
}

interface Screen {
    fun detect(id: WindowId)

    fun takeScreenshot(id: WindowId): FileId
}