package app.dokt.gradle.root

import app.dokt.generator.building.*
import app.dokt.gradle.core.ProjectPlugin
import isRoot
import org.gradle.api.Project

class DoktRootProjectPlugin : ProjectPlugin(DoktRootProjectPlugin::class) {
    override fun applyPlugin() {
        if (target.isRoot) configureProject()
        else error("Isn't applied on root project!")
    }

    override fun Project.configure() {
        register<UpdateProperties>()
        register<UpdateSettings>()

        /*task("updateBuild") {
            it.actions.add {
                val dir = projectDir.toPath()
                GradlePropertiesUpdater(dir).update()
                GradleProject.parse(dir, name = this.name).let { project ->
                    GradleBuildWriter(project).write()
                    GradleSettingsWriter(project).write()
                }
            }
        }.apply {
            description = "Update build files."
            group = "dokt"
        }*/
    }
}