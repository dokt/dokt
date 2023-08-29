package app.dokt.gradle.building

import app.dokt.generator.building.GradleProjectScanner
import app.dokt.gradle.common.LoggableBuildService
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildServiceParameters
import kotlin.system.measureTimeMillis

/**
 * Dokt [build service](https://docs.gradle.org/current/userguide/build_services.html) caches data which computation may
 * take long time and can be shared with other plugins and tasks.
 */
abstract class DoktBuildService : LoggableBuildService<DoktBuildService.Params>(DoktBuildService::class) {
    interface Params : BuildServiceParameters {
        val root: DirectoryProperty

        val settings: Property<DoktSettingsExtension>
    }

    val projectTypesByPath by lazy { GradleProjectScanner(parameters.root.get().asFile.toPath()).run {
        val ms = measureTimeMillis {
            scan()
        }
        lifecycle { "Detected ${projects.size} subprojects in $ms ms." }
        projects.toMap()
    } }

    companion object {
        const val NAME = "dokt"
    }
}
