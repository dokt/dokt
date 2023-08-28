package app.dokt.gradle.infra

import app.dokt.gradle.core.DoktJsPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktJsInfrastructurePlugin : DoktJsPlugin(DoktJsInfrastructurePlugin::class), DoktInfrastructurePlugin {
    override val pluginLabel = pluginLabel()

    override fun KotlinDependencyHandler.configureMainDependencies() = configureInfrastructureApi()
}
