package app.dokt.gradle.core

import app.dokt.gradle.common.Loggable
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.plugins.PluginContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

@Suppress("SpellCheckingInspection")
interface DoktKotlinPluginApi : Loggable {
    val platformName: String

    val Path.java: Path get() = resolve("java")

    fun PluginContainer.kotlin(module: String): Plugin<Any> = apply("org.jetbrains.kotlin.$module")

    fun PluginContainer.kotlinSerialization() = kotlin("plugin.serialization")

    val String.dokt get() = "app.dokt" to "dokt-$this"

    fun noOtherDependencies(name: String) = debug { "No other dependencies for $name." }

    fun noOtherConfigs(name: String) = debug { "No other configs for $name." }

    fun NamedDomainObjectContainer<KotlinSourceSet>.configure(dir: Path, block: KotlinSourceSet.() -> Unit) {
        val name = dir.name
        if (dir.exists()) {
            info { "Configuring $name source set." }
            getByName(name).block()
        } else debug { "Skipping $name source set." }
    }

    fun KotlinDependencyHandler.impl(groupToModule: Pair<String, String>) {
        val (group, module) = groupToModule
        debug { "Depending $group $module implementation." }
        implementation("$group:$module:_")
    }

    fun KotlinDependencyHandler.itf(groupToModule: Pair<String, String>) {
        val (group, module) = groupToModule
        debug { "Depending $group $module API." }
        api("$group:$module:_")
    }

    fun KotlinDependencyHandler.run(groupToModule: Pair<String, String>) {
        val (group, module) = groupToModule
        debug { "Depending $group $module runtime." }
        runtimeOnly("$group:$module:_")
    }

    fun KotlinDependencyHandler.jvmTestDependencies() {
        info { "Depending JVM test implementations: Dokt test and Kotest." }
        impl("test".dokt)
        impl("io.kotest" to "kotest-runner-junit5")
        jvmLoggingDependencies()
    }

    fun KotlinDependencyHandler.jvmLoggingDependencies() {
        info { "Depending JVM logging runtimes: Locback and SLF4J (JCL, JUL & Log4J bindings)." }
        run("ch.qos.logback" to "logback-classic")
        listOf("jcl-over-slf4j", "jul-to-slf4j", "log4j-over-slf4j").forEach {
            run("org.slf4j" to it)
        }
    }
}