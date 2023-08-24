package app.dokt.generator.building

import java.io.File
import java.util.*

class GradlePropertiesUpdater(dir: File) : PropertiesUpdater(dir, {}) {
    override val name = "gradle"

    override fun update(previous: SortedMap<String, String>?) =
        if (previous == null) defaults else (previous + defaults).toSortedMap()

    companion object {
        @Suppress("SpellCheckingInspection")
        private val defaults = mapOf(
            "kotlin.code.style" to "official",
            "kotlin.mpp.stability.nowarn" to "true",
            "org.gradle.caching" to "true",
            "org.gradle.configureondemand" to "true",
            "org.gradle.jvmargs" to "-Xmx2G",
            "org.gradle.logging.stacktrace" to "all",
            "org.gradle.parallel" to "true",
            "org.gradle.warning.mode" to "all"
        ).toSortedMap()
    }
}