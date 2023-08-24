package app.dokt.gradle.infra

import app.dokt.gradle.core.DoktMultiPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktMultiInfrastructurePlugin : DoktMultiPlugin(DoktMultiInfrastructurePlugin::class), DoktInfrastructurePlugin {
    override val pluginLabel = pluginLabel()

    override fun KotlinDependencyHandler.configureCommonMainDependencies() = configureInfrastructureApi()
}
