package app.dokt.gradle

import app.dokt.generator.application.*
import app.dokt.gradle.InterfaceProject.Companion.DOKT_INTERFACE
import jvmMain
import jvmTest
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_TEST_SOURCE_SET_NAME
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import java.io.File

private const val DOKT_PREFIX = "app.dokt:dokt"
private const val DOKT = "$DOKT_PREFIX:_"
private const val DOKT_TEST = "$DOKT_PREFIX-test:_"
private const val JUL_TO_SLF4J = "org.slf4j:jul-to-slf4j:_"
private const val JCL_OVER_SLF4J = "org.slf4j:jcl-over-slf4j:_"
private const val KOTLIN = "kotlin"
private const val KOTLIN_LOGGING = "io.github.microutils:kotlin-logging:_"
private const val LOGBACK = "ch.qos.logback:logback-classic:_"
private const val LOG4J_OVER_SLF4J = "org.slf4j:log4j-over-slf4j:_"
private const val TEST = "test"

/**
 * Configures application layer project for Kotlin Multiplatform.
 *
 * - Handles authorization before service call.
 * - Handles transactions.
 * - Has application services which are common to all interfaces.
 * - May have application service infrastructure for other platforms.
 */
class ApplicationProject(project: Project, srcDir: File) : MultiplatformProject(project, srcDir) {
    override val layer get() = "application"

    override fun KotlinDependencyHandler.configureCommonMain() {
        api(DOKT_APPLICATION)
    }

    companion object {
        const val SUFFIX = "-app"
        private const val DOKT_APPLICATION = "$DOKT_PREFIX-application:_"
    }
}

/**
 * Configures domain layer project for Kotlin Multiplatform.
 *
 * - Domain logic is written only using Common Kotlin code.
 * - Domain service infrastructure may be implemented in other platforms.
 */
class DomainProject(project: Project, srcDir: File) : MultiplatformProject(project, srcDir) {
    private val application by lazy { GradleApplication(this) }
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    val generatedDir = buildDir.resolve("generated")
    override val generatedCommonMainDir = generatedDir.resolve(COMMON_MAIN_SOURCE_SET_NAME)
    override val generatedCommonTestDir by lazy { generatedDir.resolve(COMMON_TEST_SOURCE_SET_NAME) }

    override val layer get() = "domain"

    override fun configureTasks() {
        task("cleanGenerated") {
            doLast { delete(generatedDir) }
        }.description = "Delete generated files."

        task(GENERATE_CODE) {
            doLast { coder.code() }
        }.description = "Generate application classes."

        task("generateDocumentation") {
            doLast { documentWriter.document() }
        }.description = "Generate Markdown documentation of the application."

        tasks.filter { it.name.startsWith("compileKotlin") }.forEach {it.dependsOn(GENERATE_CODE) }
    }

    override fun KotlinDependencyHandler.configureCommonMain() { api(DOKT_DOMAIN) }

    override fun KotlinDependencyHandler.configureCommonTest() { implementation(DOKT_DOMAIN_TEST) }

    override fun KotlinDependencyHandler.configureJvmMain() {
        api(KOTLIN_LOGGING)
        logback()
    }

    companion object {
        const val SUFFIX = "-dom"
        private const val DOKT_DOMAIN = "$DOKT_PREFIX-domain:_"
        private const val DOKT_DOMAIN_TEST = "$DOKT_PREFIX-domain-test:_"
        private const val GENERATE_CODE = "generateCode"
    }
}

/** Infrastructure project which may contain application or domain service implementations to any platform. */
interface InfrastructureProject

class InfrastructureMultiplatformProject(project: Project, srcDir: File)
    : MultiplatformProject(project, srcDir), InfrastructureProject {
    override val layer get() = "multiplatform infrastructure"
}

class InfrastructureJsProject(project: Project, srcDir: File) : JsProject(project, srcDir), InfrastructureProject {
    override val layer get() = "JS infrastructure"
}

class InfrastructureJvmProject(project: Project, srcDir: File) : JvmProject(project, srcDir), InfrastructureProject {
    override val layer get() = "JVM infrastructure"
}

/**
 * Interface application project.
 *
 * - Handles authentication.
 * - Contains runnable interface application and its implementation.
 * - Dispatches calls to application services.
 */
interface InterfaceProject {
    /* TODO apply application plugin and set application mainClass

        configure<JavaApplication> {
        val parentName = parent!!.name
        this.mainClass.set("$group.$name.${name.capitalized}${parentName.capitalized}Kt")
    }

private val String.capitalized get() = toLowerCase().capitalize()
private val String.classPackage get() = substringBeforeLast(".").substringAfterLast(".")

     */

    companion object {
        const val DOKT_INTERFACE = "$DOKT_PREFIX-interface:_"
        const val TAG = "-if"
    }
}

class InterfaceMultiplatformProject(project: Project, srcDir: File)
    : MultiplatformProject(project, srcDir), InterfaceProject {
    override val layer get() = "multiplatform interface"

    override fun applyPlugins() {
        super.applyPlugins()
        apply<ApplicationPlugin>()
    }

    override fun KotlinDependencyHandler.configureCommonMain() { implementation(DOKT_INTERFACE) }
}

class InterfaceJsProject(project: Project, srcDir: File) : JsProject(project, srcDir), InterfaceProject {
    override val layer get() = "JS interface"
}

class InterfaceJvmProject(project: Project, srcDir: File) : JvmProject(project, srcDir), InterfaceProject {
    override val layer get() = "JVM interface"

    override fun applyPlugins() {
        super.applyPlugins()
        apply<ApplicationPlugin>()
    }

    override fun mainImplementations() = listOf(DOKT_INTERFACE)
}

/** Architecture layer project */
abstract class LayerProject(project: Project, protected val srcDir: File) : Project by project {
    protected abstract val layer: String

    protected open fun applyPlugins() {
        apply<SerializationGradleSubplugin>()
        apply<MavenPublishPlugin>()
    }

    abstract fun configureKotlin()

    open fun configureTasks() {}

    fun configureLayer() {
        quiet("Configuring $layer architecture layer")
        applyPlugins()

        // TODO Workaround for dependency resolution management in settings.
        repositories {
            mavenCentral()
            mavenLocal()
        }

        configureKotlin()
        configureTasks()
    }
}

abstract class MultiplatformProject(project: Project, srcDir: File) : LayerProject(project, srcDir) {
    val commonMainKotlin by lazy { srcDir.resolve(COMMON_MAIN_SOURCE_SET_NAME).resolve(KOTLIN) }
    private val commonTestDir = srcDir.resolve(COMMON_TEST_SOURCE_SET_NAME)
    open val generatedCommonMainDir: File? = null
    open val generatedCommonTestDir: File? = null
    private val jvmMainDir = srcDir.resolve("jvmMain")
    private val jvmTestDir = srcDir.resolve("jvmTest")

    private val hasCommonTests = commonTestDir.exists()
    private val hasJvmTests = jvmTestDir.exists()

    override fun applyPlugins() {
        apply<KotlinMultiplatformPluginWrapper>()
        super.applyPlugins()
    }

    @Suppress("unused_variable")
    override fun configureKotlin() {
        configure<KotlinMultiplatformExtension> {
            jvm {
                if (hasCommonTests || hasJvmTests) testRuns[TEST].executionTask.configure {
                    useJUnitPlatform()
                }
            }

            sourceSets {
                val commonMain by getting {
                    dependencies { configureCommonMain() }
                    generatedCommonMainDir?.let { kotlin.srcDir(it) }
                }

                if (hasCommonTests) {
                    val commonTest by getting {
                        dependencies { configureCommonTest() }
                        generatedCommonTestDir?.let { kotlin.srcDir(it) }
                    }
                }

                if (jvmMainDir.exists()) { jvmMain { configureJvmMain() } }

                if (hasJvmTests) { jvmTest { configureJvmTest() } }
            }
        }
    }

    protected open fun KotlinDependencyHandler.configureCommonMain() {
        api(DOKT)
        api(KOTLIN_LOGGING)
    }

    protected open fun KotlinDependencyHandler.configureCommonTest() {
        implementation(DOKT_TEST)
        logback()
    }

    protected open fun KotlinDependencyHandler.configureJvmMain() = logback()

    private fun KotlinDependencyHandler.configureJvmTest() {
        implementation(DOKT_TEST)
        logback()
    }

    protected fun KotlinDependencyHandler.logback() {
        runtimeOnly(LOGBACK)
        runtimeOnly(JCL_OVER_SLF4J)
        runtimeOnly(JUL_TO_SLF4J)
        runtimeOnly(LOG4J_OVER_SLF4J)
    }
}

abstract class JsProject(project: Project, srcDir: File) : LayerProject(project, srcDir) {
    override fun applyPlugins() {
        apply<KotlinJsPluginWrapper>()
        super.applyPlugins()
    }

    override fun configureKotlin() {}
}

abstract class JvmProject(project: Project, srcDir: File) : LayerProject(project, srcDir) {
    private val testDir = srcDir.resolve(TEST)

    private val hasTests = testDir.exists()

    override fun applyPlugins() {
        apply<KotlinPluginWrapper>()
        super.applyPlugins()
    }

    override fun configureKotlin() {
        if (hasTests) tasks.withType<Test>().configureEach { useJUnitPlatform() }

        dependencies {
            mainImplementations().forEach { add("implementation", it) }
            mainRuntimes().forEach { add("runtimeOnly", it) }
            if (hasTests) {
                testImplementations().forEach { add("testImplementation", it) }
                testRuntimes().forEach { add("testRuntimeOnly", it) }
            }
        }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("maven") {
                    from(components[KOTLIN])
                }
            }
        }
    }

    protected open fun mainImplementations() = listOf(DOKT, KOTLIN_LOGGING)

    protected open fun mainRuntimes() = listOf(LOGBACK, JCL_OVER_SLF4J, JUL_TO_SLF4J, LOG4J_OVER_SLF4J)

    protected open fun testImplementations() = listOf(DOKT_TEST)

    protected open fun testRuntimes() = listOf(LOGBACK, JCL_OVER_SLF4J, JUL_TO_SLF4J, LOG4J_OVER_SLF4J)
}
