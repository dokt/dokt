package app.dokt.generator.building

import java.nio.file.Path

class GradlePropertiesUpdater(override val directory: Path) : PropertiesUpdater() {
    override val name = "gradle"

    override fun updateProperties() { map += defaults }

    companion object {
        private val defaults = mapOf(
            "kotlin.code.style" to "official",
            "kotlin.mpp.stability.nowarn" to "true",
            "org.gradle.caching" to "true",
            "org.gradle.configureondemand" to "true",
            "org.gradle.jvmargs" to "-Xmx2G",
            "org.gradle.logging.stacktrace" to "all",
            "org.gradle.parallel" to "true",
            "org.gradle.warning.mode" to "all"
        )
    }
}