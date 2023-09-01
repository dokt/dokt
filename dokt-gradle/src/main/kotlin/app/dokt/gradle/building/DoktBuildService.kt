package app.dokt.gradle.building

import app.dokt.generator.building.GradleProjectScanner
import app.dokt.gradle.common.LoggableBuildService
import app.dokt.gradle.common.path
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildServiceParameters
import java.nio.file.Path
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

    /** Project types by path */
    val projectTypes by lazy { GradleProjectScanner(rootDir).run {
        val ms = measureTimeMillis {
            scan()
        }
        lifecycle { "Detected ${projects.size} subprojects in $ms ms." }
        projects.toMap()
    } }

    val projectPaths by lazy { projectTypes.keys.toList() }

    val rootDir: Path by lazy { parameters.root.path }

    val settingsExtension: DoktSettingsExtension by lazy { parameters.settings.get() }

    companion object {
        const val NAME = "dokt"
    }
}
