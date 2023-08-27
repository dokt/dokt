package app.dokt.gradle.building

import app.dokt.gradle.building.task.GenerateSettings
import app.dokt.gradle.building.task.UpdateProperties
import app.dokt.gradle.building.task.UpdateSettings
import app.dokt.gradle.common.ProjectPlugin
import isRoot
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer

class DoktRootPlugin : ProjectPlugin(DoktRootPlugin::class) {
    override val pluginLabel = "Dokt root project"

    override fun Project.validate() {
        require(isRoot) { "Apply DoktRootPlugin only on root project!" }
    }

    override fun TaskContainer.registerTasks() {
        //register<GenerateBuild>()       // TODO remove deprecated task
        register<GenerateSettings>()    // TODO remove deprecated task
        register<UpdateProperties>()
        register<UpdateSettings>()
    }
}
