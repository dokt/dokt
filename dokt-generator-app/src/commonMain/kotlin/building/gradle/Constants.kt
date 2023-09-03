package app.dokt.generator.building.gradle

import app.dokt.common.Version
import app.dokt.generator.DOKT_VER
import app.dokt.generator.REFRESH_VER

private fun applyPlugin(id: String, version: Version) = """id("$id") version "$version""""

const val DOKT_ID = "app.dokt"
const val REFRESH_ID = "de.fayard.refreshVersions"

val APPLY_DOKT = applyPlugin(DOKT_ID, DOKT_VER)
val APPLY_REFRESH = applyPlugin(REFRESH_ID, REFRESH_VER)

const val SETTINGS_SCRIPT = "settings.gradle.kts"
