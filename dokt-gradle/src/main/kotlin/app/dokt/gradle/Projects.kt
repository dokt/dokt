package app.dokt.gradle

import app.dokt.common.lowerFirst
import app.dokt.generator.building.ProjectType
import app.dokt.gradle.app.DoktApplicationPlugin
import app.dokt.gradle.building.DoktRootPlugin
import app.dokt.gradle.domain.DoktDomainPlugin
import app.dokt.gradle.domain.DoktDomainsPlugin
import app.dokt.gradle.iface.DoktJsInterfacePlugin
import app.dokt.gradle.iface.DoktJvmInterfacePlugin
import app.dokt.gradle.iface.DoktKtorServerPlugin
import app.dokt.gradle.iface.DoktMultiInterfacePlugin
import app.dokt.gradle.iface.DoktSwingPlugin
import app.dokt.gradle.iface.DoktSwtPlugin
import app.dokt.gradle.infra.DoktJsInfrastructurePlugin
import app.dokt.gradle.infra.DoktJvmInfrastructurePlugin
import app.dokt.gradle.infra.DoktMultiInfrastructurePlugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.io.File

val Project.buildDirAsFile get(): File = layout.buildDirectory.asFile.get()
val Project.commonMainDir get() = buildDirAsFile.resolve(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
val Project.commonTestDir get() = buildDirAsFile.resolve(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME)

inline fun <reified T : Task> TaskContainer.register(): TaskProvider<T> = T::class.run {
    register(simpleName!!.lowerFirst, java)
}

val ProjectType.plugin get() = when (this) {
    ProjectType.APPLICATION -> DoktApplicationPlugin::class
    ProjectType.DOMAIN -> DoktDomainPlugin::class
    ProjectType.DOMAINS -> DoktDomainsPlugin::class
    ProjectType.INFRASTRUCTURE -> DoktMultiInfrastructurePlugin::class
    ProjectType.INFRASTRUCTURE_JS -> DoktJsInfrastructurePlugin::class
    ProjectType.INFRASTRUCTURE_JVM -> DoktJvmInfrastructurePlugin::class
    ProjectType.INTERFACE -> DoktMultiInterfacePlugin::class
    ProjectType.INTERFACE_JS -> DoktJsInterfacePlugin::class
    ProjectType.INTERFACE_JVM -> DoktJvmInterfacePlugin::class
    ProjectType.KTOR_SERVER -> DoktKtorServerPlugin::class
    ProjectType.ROOT -> DoktRootPlugin::class
    ProjectType.SWING -> DoktSwingPlugin::class
    ProjectType.SWT -> DoktSwtPlugin::class
}
