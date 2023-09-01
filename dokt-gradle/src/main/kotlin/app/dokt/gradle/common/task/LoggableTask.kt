package app.dokt.gradle.common.task

import app.dokt.gradle.common.Loggable
import app.dokt.gradle.common.LoggableWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import kotlin.reflect.KClass

abstract class LoggableTask(
    type: KClass<out LoggableTask>,
    private val group: Group,
    private val description: String = "") :
    DefaultTask(), Loggable by LoggableWrapper(type) {

    /** Predefined task group. */
    @Suppress("SpellCheckingInspection")
    enum class Group(val value: String) {
        Build(BasePlugin.BUILD_GROUP),
        /**
         * E.g. used in [org.gradle.buildinit.plugins.BuildInitPlugin] and [org.gradle.buildinit.plugins.WrapperPlugin]
         */
        BuildSetup("Build Setup"),
        Documentation("documentation"),
        Help("help"),
        Publishing("publishing"),
        Run("run"),
        Verification("verification")
    }

    final override fun getGroup() = group.value

    override fun getDescription() = description
}
