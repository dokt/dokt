package app.dokt.gradle.building.task

import app.dokt.generator.building.GradleSettingsUpdater
import app.dokt.gradle.common.task.UpdateFile
import org.gradle.api.tasks.CacheableTask

// TODO dependsOn(":refreshVersions")?
@CacheableTask
abstract class UpdateSettings : UpdateFile(UpdateSettings::class) {
    init {
        init(GradleSettingsUpdater(project.projectDir))
    }
}
