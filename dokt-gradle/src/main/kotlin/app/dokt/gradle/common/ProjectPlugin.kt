package app.dokt.gradle.common

import app.dokt.common.*
import org.gradle.api.*
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.*
import src
import java.nio.file.Path
import kotlin.reflect.KClass

abstract class ProjectPlugin(type: KClass<out ProjectPlugin>) : LoggablePlugin<Project>(type) {
    protected val project get() = pluginTarget

    private val src by lazy { project.src() }

    protected fun src(sourceSet: String): Path = src.resolve(sourceSet)

    abstract val pluginLabel: String

    /** Configuring project */
    override fun Project.applyPlugin() {
        lifecycle { "Applying $pluginLabel plugin" }
        configureFirst()

        debug { "Applying plugins." }
        plugins.applyPlugins()

        debug { "Configuring extensions." }
        configureExtensions()

        debug { "Registering tasks." }
        tasks.registerTasks()

        debug { "Last configurations." }
        configureLast()
    }

    /** This to configure last in project */
    protected open fun Project.configureFirst() {
        debug { "No first configurations." }
    }

    protected open fun PluginContainer.applyPlugins() {
        debug { "No plugins defined." }
    }

    protected open fun Project.configureExtensions() {
        debug { "Extensions not configured." }
    }

    protected open fun TaskContainer.registerTasks() {
        debug { "No tasks defined." }
    }

    protected inline fun <reified T : Task> TaskContainer.register(): TaskProvider<T> {
        debug { "Registering ${T::class.simpleName} task" }
        return T::class.run {
            register(simpleName!!.lowerFirst, java)
        }
    }

    /** This to configure last in project */
    protected open fun Project.configureLast() {
        debug { "No last configurations." }
    }

}