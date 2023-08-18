package app.dokt.gradle.core

import app.dokt.gradle.register
import org.gradle.api.*
import kotlin.reflect.KClass

abstract class ProjectPlugin(type: KClass<out ProjectPlugin>) : LoggablePlugin<Project>(type) {
    override fun applyPlugin() = configureProject()

    protected fun configureProject() {
        debug { "Configure ${target.path} project." }
        target.configure()
    }

    /** Configure project */
    protected abstract fun Project.configure()

    protected inline fun <reified T : Task> Project.register() {
        debug { "Registering ${T::class.simpleName} task" }
        tasks.register<T>()
    }
}