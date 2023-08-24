package app.dokt.gradle.iface

import app.dokt.gradle.core.DoktJsPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktJsInterfacePlugin : DoktJsPlugin(DoktJsInterfacePlugin::class), DoktInterfacePlugin {
    override val pluginLabel = pluginLabel()

    override fun KotlinDependencyHandler.configureMainDependencies() = configureInterfaceApi()
}
