package app.dokt.gradle.iface

import app.dokt.gradle.core.DoktKotlinPluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

interface DoktInterfacePlugin : DoktKotlinPluginApi {
    fun pluginLabel() = "Dokt $platformName interface layer"

    fun KotlinDependencyHandler.configureInterfaceApi() {
        info { "Depending Dokt interface API" }
        itf("interface".dokt)
    }
}