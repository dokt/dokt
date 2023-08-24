package app.dokt.gradle.root

import app.dokt.generator.building.GradlePropertiesUpdater
import app.dokt.gradle.core.UpdateFile
import org.gradle.api.tasks.CacheableTask

@CacheableTask
abstract class UpdateProperties : UpdateFile(UpdateProperties::class) {
    init {
        init(GradlePropertiesUpdater(project.projectDir))
    }
}