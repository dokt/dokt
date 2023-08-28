package app.dokt.gradle.domain

import app.dokt.gradle.core.DoktMultiPlugin
import app.dokt.gradle.domain.task.GenerateCommonMain
import app.dokt.gradle.domain.task.GenerateCommonTest
import app.dokt.gradle.domain.task.GenerateDocumentation
import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktDomainPlugin : DoktMultiPlugin(DoktDomainPlugin::class) {
    override val pluginLabel = "Dokt domain layer"

    override fun KotlinDependencyHandler.configureCommonMainDependencies() {
        info { "Depending Dokt domain API" }
        itf("domain".dokt)
        warn { "TODO: Add other domain APIs" }
        /*domainDependencies.forEach { TODO
            api(project(":domain:$it"))
        }*/
    }

    override fun KotlinDependencyHandler.configureCommonTestDependencies() = configureJvmTestDependencies()

    override fun KotlinDependencyHandler.configureJvmMainDependencies() {
        info { "Depending Dokt infrastructure API" }
        itf("infrastructure".dokt)
    }

    override fun KotlinDependencyHandler.configureJvmTestDependencies() {
        info { "Depending Dokt domain test implementation" }
        impl("domain-test".dokt)
    }

    override fun TaskContainer.registerTasks() {
        /*task("cleanGenerated", "Delete generated files.") { TODO Exists automatically when input & output is ok.
            delete(commonMainDir)
            delete(commonTestDir)
        }*/

        //val generateDomain =
            register<GenerateCommonMain>()

        register<GenerateCommonTest>()

        register<GenerateDocumentation>()

        // TODO tasks.getByName("build") { it.dependsOn(generateDomain) }
    }
}
