package app.dokt.gradle.app

import app.dokt.gradle.core.DoktMultiPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

class DoktApplicationPlugin : DoktMultiPlugin(DoktApplicationPlugin::class) {
    override val pluginLabel = "Dokt application layer"

    override fun KotlinDependencyHandler.configureCommonMainDependencies() {
        info { "Depending Dokt application API" }
        itf("application".dokt)
    }
}
