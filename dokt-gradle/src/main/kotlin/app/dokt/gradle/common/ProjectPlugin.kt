package app.dokt.gradle.common

import app.dokt.common.lowerFirst
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.src
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
        registerTasks()
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

    protected open fun Project.registerTasks() {
        debug { "No tasks defined." }
    }

    protected open fun Project.tasks(registerTasks: TaskContainer.() -> Unit) {
        tasks.apply(registerTasks)
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

    protected inline fun <reified T : Task> TaskContainer.register(crossinline configureAction: T.() -> Unit):
        TaskProvider<T> {
        debug { "Registering ${T::class.simpleName} task with configure action." }
        return T::class.run {
            register(simpleName!!.lowerFirst, java) { it.configureAction() }
        }
    }

    /** This to configure last in project */
    protected open fun Project.configureLast() {
        debug { "No last configurations." }
    }

}
