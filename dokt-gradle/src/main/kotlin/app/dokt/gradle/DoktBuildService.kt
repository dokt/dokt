package app.dokt.gradle

import app.dokt.gradle.core.LoggableBuildService
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildServiceParameters

/** https://docs.gradle.org/current/userguide/build_services.html */
abstract class DoktBuildService : LoggableBuildService<DoktBuildService.Params>(DoktBuildService::class) {
    interface Params : BuildServiceParameters {
        val port: Property<Int>
        val resources: DirectoryProperty
    }
}