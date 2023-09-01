package app.dokt.gradle.core.task

import app.dokt.gradle.building.DoktBuildService
import app.dokt.gradle.building.DoktSettingsExtension
import app.dokt.gradle.common.task.LoggableTask
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Internal
import kotlin.reflect.KClass

abstract class DoktServiceTask(type: KClass<out DoktServiceTask>, group: Group, description: String) :
    LoggableTask(type, group, description) {

    @Suppress("UnstableApiUsage")
    @get:ServiceReference(DoktBuildService.NAME)
    abstract val doktServiceReference: Property<DoktBuildService>

    @get:Internal
    protected val doktService: DoktBuildService get() = doktServiceReference.get()

    @get:Internal
    protected val settingsExtension: DoktSettingsExtension get() = doktService.settingsExtension
}
