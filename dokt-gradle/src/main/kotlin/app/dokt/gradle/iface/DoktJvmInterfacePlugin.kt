package app.dokt.gradle.iface

import app.dokt.gradle.core.DoktJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import kotlin.reflect.KClass

open class DoktJvmInterfacePlugin(type: KClass<out DoktJvmInterfacePlugin> = DoktJvmInterfacePlugin::class) :
    DoktJvmPlugin(type), DoktInterfacePlugin {
    override val pluginLabel get() = pluginLabel()

    final override fun KotlinDependencyHandler.configureMainDependencies() = configureInterfaceApi()
}
