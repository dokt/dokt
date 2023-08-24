package app.dokt.gradle.core

import app.dokt.gradle.common.ProjectPlugin
import org.gradle.api.*
import org.gradle.api.plugins.PluginContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass

/** Gradle plugin with Kotlin features. */
abstract class DoktKotlinPlugin<E : KotlinProjectExtension>(
    type: KClass<out DoktKotlinPlugin<E>>,
    protected val kotlinModule: String,
    private val kotlinProjectExtensionClass: KClass<E>
) : ProjectPlugin(type), DoktKotlinPluginApi
{
    final override fun PluginContainer.applyPlugins() {
        kotlin(kotlinModule)
        kotlinSerialization()
    }

    final override fun Project.configureExtensions() {
        extensions.configure(kotlinProjectExtensionClass.java) {
            it.configureKotlin()

            it.sourceSets.configureSourceSets()
        }
    }

    protected abstract fun E.configureKotlin()

    protected abstract fun NamedDomainObjectContainer<KotlinSourceSet>.configureSourceSets()
}