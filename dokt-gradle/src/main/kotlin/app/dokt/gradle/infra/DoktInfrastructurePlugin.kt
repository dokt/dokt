package app.dokt.gradle.infra

import app.dokt.gradle.core.DoktKotlinPluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface DoktInfrastructurePlugin : DoktKotlinPluginApi {
    fun pluginLabel() = "Dokt $platformName infrastructure"

    fun KotlinDependencyHandler.configureInfrastructureApi() {
        info { "Depending Dokt infrastructure API" }
        itf("infrastructure".dokt)
    }
}
