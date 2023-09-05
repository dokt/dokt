package app.dokt.gradle.building

import app.dokt.generator.building.gradle.ProjectScanner
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

    /** Subproject type-leaf pair by include path */
    private val subprojectTypeLeafPairByIncludePath by lazy { ProjectScanner(rootDir).run {
        val ms = measureTimeMillis { scan() }
        lifecycle { "Detected ${subprojects.size} subprojects in $ms ms." }
        subprojects.toMap()
    } }

    /** Project types by path */
    val subprojectTypesByPath by lazy { subprojectTypeLeafPairByIncludePath.map { (includePath, typeLeafPair) ->
        ":$includePath" to typeLeafPair.first }.toMap()
    }

    val subprojectsToInclude by lazy { subprojectTypeLeafPairByIncludePath.filterValues { it.second }.keys }

    val rootDir: Path by lazy { parameters.root.path }

    val settingsExtension: DoktSettingsExtension by lazy { parameters.settings.get() }

    companion object {
        const val NAME = "dokt"
    }
}
