package app.dokt.gradle.infra

import app.dokt.gradle.core.DoktJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktJvmInfrastructurePlugin : DoktJvmPlugin(DoktJvmInfrastructurePlugin::class), DoktInfrastructurePlugin {
    override val pluginLabel = pluginLabel()

    override fun KotlinDependencyHandler.configureMainDependencies() = configureInfrastructureApi()
}