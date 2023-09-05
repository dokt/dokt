package app.dokt.gradle.building

import app.dokt.common.lastModifiedMillis
import app.dokt.generator.building.gradle.SETTINGS_SCRIPT
import app.dokt.gradle.building.task.UpdateSettings
import app.dokt.gradle.building.task.UpdateProperties
import app.dokt.gradle.common.ProjectPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.isRoot
import org.gradle.kotlin.dsl.projectDirectory

class DoktRootPlugin : ProjectPlugin(DoktRootPlugin::class) {
    override val pluginLabel = "Dokt root project"

    override fun Project.validate() {
        require(isRoot) { "Apply DoktRootPlugin only on root project!" }
    }

    override fun Project.registerTasks() {
        tasks {
            register<UpdateProperties>()
            register<UpdateSettings> {
                projectDirectory.resolve(SETTINGS_SCRIPT).apply {
                    modified.set(lastModifiedMillis)
                    file.set(toFile())
                }
            }
        }
    }
}
