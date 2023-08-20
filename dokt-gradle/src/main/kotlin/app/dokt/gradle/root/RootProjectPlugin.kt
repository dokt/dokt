package app.dokt.gradle.root

import app.dokt.generator.building.*
import app.dokt.gradle.core.ProjectPlugin
import app.dokt.gradle.register
import isRoot
import org.gradle.api.Project

class RootProjectPlugin : ProjectPlugin(RootProjectPlugin::class) {
    override fun applyPlugin() {
        if (target.isRoot) configureProject()
        else error("Isn't applied on root project!")
    }

    override fun Project.configure() {
        /*subprojects {
            it.plugins.apply(DoktPlugin::class.java)
        }*/

        projectDir

        register<UpdateSettings>()

        task("updateBuild") {
            it.actions.add {
                val dir = projectDir.toPath()
                GradlePropertiesUpdater(dir).update()
                GradleProject.parse(dir, name = this.name).let { project ->
                    GradleBuildWriter(project).write()
                    GradleSettingsWriter(project).write()
                }
            }
        }.description = "Update build files."


        /*task("updateSettings") {
            //it.dependsOn(":refreshVersions")
        }.description = "Updates settings file."*/
    }
}