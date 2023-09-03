package app.dokt.gradle.building.task

import app.dokt.generator.building.GradlePropertiesUpdater
import app.dokt.gradle.common.task.UpdateFile
import org.gradle.api.tasks.CacheableTask

@CacheableTask
abstract class UpdateProperties : UpdateFile(UpdateProperties::class, Group.BuildSetup) {
    init {
        init(GradlePropertiesUpdater(project.projectDir))
    }
}
