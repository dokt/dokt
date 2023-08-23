package app.dokt.gradle.root

import app.dokt.generator.building.GradleSettingsUpdater
import app.dokt.gradle.REFRESH_VER
import app.dokt.gradle.core.UpdateFile
import org.gradle.api.tasks.CacheableTask

// TODO dependsOn(":refreshVersions")?
@CacheableTask
abstract class UpdateSettings : UpdateFile(UpdateSettings::class) {
    init {
        init(GradleSettingsUpdater(project.projectDir, REFRESH_VER))
    }
}