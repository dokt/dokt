package app.dokt.gradle.iface

import app.dokt.gradle.core.DoktMultiPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import kotlin.reflect.KClass

open class DoktMultiInterfacePlugin(type: KClass<out DoktMultiInterfacePlugin> = DoktMultiInterfacePlugin::class) :
    DoktMultiPlugin(type), DoktInterfacePlugin
{
    override val pluginLabel = pluginLabel()

    final override fun KotlinDependencyHandler.configureCommonMainDependencies() = configureInterfaceApi()
}
