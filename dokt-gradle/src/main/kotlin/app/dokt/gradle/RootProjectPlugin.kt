package app.dokt.gradle

import app.dokt.generator.building.*
import org.gradle.api.*

@Suppress("unused")
class RootProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) = with (target) {
        if (equals(rootProject)) {
            task("updateBuild") {
                it.actions.add {
                    val dir = projectDir.toPath()
                    GradlePropertiesUpdater(dir).update()
                    GradleProject.parse(dir, name = this@with.name).let { project ->
                        GradleBuildWriter(project).write()
                        GradleSettingsWriter(project).write()
                    }
                }
            }.description = "Update build files."
        } else logger.warn("Apply Dokt plugin on root project!")
    }
}