package app.dokt.generator.building

import app.dokt.common.Props
import java.io.File

class GradlePropertiesUpdater(dir: File) : PropertiesUpdater(dir, {}) {
    override val name get() = "gradle"

    override fun update(previous: Props?) =
        if (previous == null) defaults else (previous + defaults)

    companion object {
        // https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
        @Suppress("SpellCheckingInspection")
        private val defaults = mapOf(
            p("kotlin.code.style", "official",
                "https://kotlinlang.org/docs/code-style-migration-guide.html"),
            p("org.gradle.caching", true,
                "Gradle's properties",
                "https://docs.gradle.org/current/userguide/build_environment.html", "",
                "When set to true, Gradle will reuse task outputs from any previous build, when possible,",
                "resulting in much faster builds. che. By default, the build cache is not enabled."),
            p("org.gradle.configureondemand", true,
                "Enables incubating configuration on demand, where Gradle will attempt to configure only",
                        "necessary projects. Default is false."),
            p("org.gradle.jvmargs", "-Xmx2G",
                "Specifies the JVM arguments used for the Gradle Daemon. The setting is particularly useful",
                "for configuring JVM memory settings for build performance. This does not affect the JVM settings",
                "for the Gradle client VM. The default is -Xmx512m \"-XX:MaxMetaspaceSize=384m\"."),
            p("org.gradle.logging.level", "lifecycle",
                "When set to quiet, warn, lifecycle, info, or debug, Gradle will use this log level.",
                "The values are not case-sensitive. The lifecycle level is the default."),
            p("org.gradle.logging.stacktrace", "internal",
                "Specifies whether stacktraces should be displayed as part of the build result upon",
                "an exception. When set to internal, a stacktrace is present in the output only in case of",
                "internal exceptions. When set to all or full, a stacktrace is present in the output for",
                "all exceptions and build failures. Using full doesn't truncate the stacktrace, which leads",
                "to a much more verbose output. Default is internal."),
            p("org.gradle.parallel", true,
                "When configured, Gradle will fork up to org.gradle.workers.max JVMs to execute projects",
                "in parallel. Default is false."),
            p("org.gradle.warning.mode", "summary",
                "When set to all, summary or none, Gradle will use different warning type display.",
                "Default is summary.")
        )

        private fun p(key: String, value: Any, vararg comments: String) =
            key to (listOf("#") + comments.map { "# $it" } to "$value")
    }
}