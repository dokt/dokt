package app.dokt.gradle

import commonMainDependencies
import commonTestDependencies
import jvmMainDependencies
import jvmTestDependencies
import org.gradle.api.*
import org.gradle.api.plugins.PluginContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

@Deprecated("Use separate plugins")
class DoktPlugin : Plugin<Any> {

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    override fun apply(target: Any) {
        if (target is Project) target.configure()
    }

    private fun KotlinDependencyHandler.jvmTestDependencies() {
        implementation("test".dokt)
        implementation(KOTEST)
        logDependencies()
    }

    private fun KotlinDependencyHandler.logDependencies() {
        runtimeOnly(LOGBACK)
        runtimeOnly(SLF4J_JCL)
        runtimeOnly(SLF4J_JUL)
        runtimeOnly(SLF4J_LOG4J)
    }

    private fun KotlinMultiplatformExtension.jvmWithTests(java: Boolean = false) {
        jvm {
            if (java) withJava()
            testRuns.getByName("test").executionTask.configure {
                it.ignoreFailures = true
                it.useJUnitPlatform()
            }
        }

        commonTestDependencies {
            jvmTestDependencies()
        }

        jvmMainDependencies {
            logDependencies()
        }

        jvmTestDependencies {
            jvmTestDependencies()
        }
    }

    private fun PluginContainer.kotlin(module: String) = apply("org.jetbrains.kotlin.$module")

    private fun Project.configure() {
        if (equals(rootProject)) configureRoot()
        else {
            if (name.startsWith("dom")) { // Skip domain(s) folder
                println("> Dokt is skipping $name project")
                return
            }
            else if (path.contains("dom")) configureDomain()
        }
    }

    private fun Project.configureDomain() {
        println("> Dokt configures $name domain")

        with(plugins) {
            kotlin("multiplatform")
            kotlin("plugin.serialization")
        }

        extensions.configure(KotlinMultiplatformExtension::class.java) {
            it.jvmWithTests()

            it.commonMainDependencies {
                api("domain".dokt)
                /*domainDependencies.forEach { TODO
                    api(project(":domain:$it"))
                }*/
            }

            it.commonTestDependencies {
                implementation("domain-test".dokt)
            }

            it.jvmMainDependencies {
                api("infrastructure".dokt)
            }

            it.jvmTestDependencies {
                implementation("domain-test".dokt)
            }
        }
    }

    private fun Project.configureRoot() {
        println("> Dokt configures root project")

        allprojects {
            it.plugins.apply(DoktPlugin::class.java)
        }

        task("updateSettings", "Updates settings file.") {
            //it.inputs // TODO for cache
            //it.outputs // TODO for cache
            println("> Dokt updating settings") // TODO
            //it.dependsOn(":refreshVersions")
        }
    }

    private fun Project.task(name: String, description: String, action: (Task) -> Unit): Task = task(name) {
        it.group = "dokt"
        it.description = description
        it.actions.add(action)
    }

    private val String.dokt get() = "app.dokt:dokt-$this:_"

    companion object {
        //#region Libraries
        private const val DOKT = "app.dokt:dokt"
        private const val KOTEST = "io.kotest:kotest-runner-junit5:_" // Testing.kotest.runner.junit5
        private const val KTOR = "io.ktor:ktor"
        private const val LOGBACK = "ch.qos.logback:logback-classic:_"
        private const val SLF4J_JCL = "org.slf4j:jcl-over-slf4j:_"
        private const val SLF4J_JUL = "org.slf4j:jul-to-slf4j:_"
        private const val SLF4J_LOG4J = "org.slf4j:log4j-over-slf4j:_"
        private val String.dokt get() = "app.dokt:dokt-$this:_"
        //#endregion

        //#region Configurations
        private const val API = "api"
        private const val IMPLEMENTATION = "implementation"
        private const val RUNTIME_ONLY = "runtimeOnly"
        private const val TEST_IMPLEMENTATION = "testImplementation"
        private const val TEST_RUNTIME_ONLY = "testRuntimeOnly"
        //#endregion
    }
}