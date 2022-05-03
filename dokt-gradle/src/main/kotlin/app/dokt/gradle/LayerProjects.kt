package app.dokt.gradle

import app.dokt.generator.application.KotlinPoetApplicationCoder
import app.dokt.generator.application.MarkDownApplicationDocumentWriter
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
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

private const val DOKT = "app.dokt:dokt:_"
private const val DOKT_TEST = "app.dokt:dokt-test:_"
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
class ApplicationProject(project: Project) : MultiplatformProject(project) {
    override val layer get() = "application"
}

/**
 * Configures domain layer project for Kotlin Multiplatform.
 *
 * - Domain logic is written only using Common Kotlin code.
 * - Domain service infrastructure may be implemented in other platforms.
 */
class DomainProject(project: Project) : MultiplatformProject(project) {
    private val application by lazy { GradleApplication(this) }
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    val generatedDir = buildDir.resolve("generated")
    val generatedCommonMainDir = generatedDir.resolve(COMMON_MAIN_SOURCE_SET_NAME)
    val generatedCommonTestDir by lazy { generatedDir.resolve(COMMON_TEST_SOURCE_SET_NAME) }

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

        tasks.filter {it.name.startsWith("compileKotlin") }.forEach {it.dependsOn(GENERATE_CODE) }
    }

    override fun KotlinSourceSet.configureCommonMain() {
        dependencies {
            implementation(KOTLIN_LOGGING)
            implementation(KotlinX.datetime)
            implementation(KotlinX.serialization.core)
            implementation(DOKT)
        }
        kotlin.srcDir(generatedCommonMainDir)
    }

    override fun KotlinSourceSet.configureCommonTest() {
        dependencies {
            implementation(DOKT_TEST)
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.framework.api)
            implementation(KotlinX.serialization.json)
            runtimeOnly(LOGBACK)
            runtimeOnly(Testing.kotest.runner.junit5)
            runtimeOnly(JCL_OVER_SLF4J)
            runtimeOnly(LOG4J_OVER_SLF4J)
        }
        kotlin.srcDir(generatedCommonTestDir)
    }

    companion object {
        private const val GENERATE_CODE = "generateCode"
    }
}

/** Infrastructure project which may contain application or domain service implementations to any platform. */
interface InfrastructureProject

class InfrastructureMultiplatformProject(project: Project) : MultiplatformProject(project), InfrastructureProject {
    override val layer get() = "multiplatform infrastructure"
}

class InfrastructureJsProject(project: Project) : JsProject(project), InfrastructureProject {
    override val layer get() = "JS infrastructure"
}

class InfrastructureJvmProject(project: Project) : JvmProject(project), InfrastructureProject {
    override val layer get() = "JVM infrastructure"
}

/**
 * Interface application project.
 *
 * - Handles authentication.
 * - Contains runnable interface application and its implementation.
 * - Dispatches calls to application services.
 */
interface InterfaceProject

class InterfaceMultiplatformProject(project: Project) : MultiplatformProject(project), InterfaceProject {
    override val layer get() = "multiplatform interface"

    override fun applyPlugins() {
        super.applyPlugins()
        apply<ApplicationPlugin>()
    }
}

class InterfaceJsProject(project: Project) : JsProject(project), InterfaceProject {
    override val layer get() = "JS interface"
}

class InterfaceJvmProject(project: Project) : JvmProject(project), InterfaceProject {
    override val layer get() = "JVM interface"

    override fun applyPlugins() {
        super.applyPlugins()
        apply<ApplicationPlugin>()
    }
}

/** Architecture layer project */
abstract class LayerProject(project: Project) : Project by project {
    protected abstract val layer: String

    protected val srcDir = projectDir.resolve("src")

    protected open fun applyPlugins() {
        apply<SerializationGradleSubplugin>()
        apply<MavenPublishPlugin>()
    }

    abstract fun configureKotlin()

    open fun configureTasks() {}

    fun configureLayer() {
        quiet("Configuring $layer architecture layer")
        applyPlugins()
        configureKotlin()
        configureTasks()
    }
}

abstract class MultiplatformProject(project: Project) : LayerProject(project) {
    val commonMainKotlin by lazy { srcDir.resolve(COMMON_MAIN_SOURCE_SET_NAME).resolve(KOTLIN) }
    private val commonTestDir = srcDir.resolve(COMMON_TEST_SOURCE_SET_NAME)
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
                    configureCommonMain()
                }

                if (hasCommonTests) {
                    val commonTest by getting {
                        configureCommonTest()
                    }
                }

                if (jvmMainDir.exists()) {
                    val jvmMain by getting {
                        dependencies {
                            implementation(JUL_TO_SLF4J)
                            runtimeOnly(LOGBACK)
                        }
                    }
                }

                if (hasJvmTests) {
                    val jvmMain by getting {
                        dependencies {
                            runtimeOnly(LOGBACK)
                        }
                    }
                }
            }
        }
    }

    protected open fun KotlinSourceSet.configureCommonMain() {
        dependencies {
            implementation(KOTLIN_LOGGING)
            implementation(KotlinX.datetime)
            implementation(KotlinX.serialization.core)
            implementation(DOKT)
        }
    }

    protected open fun KotlinSourceSet.configureCommonTest() {
        dependencies {
            implementation(DOKT_TEST)
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.framework.api)
            implementation(KotlinX.serialization.json)
            runtimeOnly(LOGBACK)
            runtimeOnly(Testing.kotest.runner.junit5)
            runtimeOnly(JCL_OVER_SLF4J)
            runtimeOnly(LOG4J_OVER_SLF4J)
        }
    }
}

abstract class JsProject(project: Project) : LayerProject(project) {
    override fun applyPlugins() {
        apply<KotlinJsPluginWrapper>()
        super.applyPlugins()
    }

    override fun configureKotlin() {}
}

abstract class JvmProject(project: Project) : LayerProject(project) {
    private val testDir = srcDir.resolve(TEST)

    private val hasTests = testDir.exists()

    override fun applyPlugins() {
        apply<KotlinPluginWrapper>()
        super.applyPlugins()
    }

    override fun configureKotlin() {
        if (hasTests) tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }

        dependencies {
            implementation(KOTLIN_LOGGING)
            implementation(JUL_TO_SLF4J)
            implementation(KotlinX.datetime)
            implementation(KotlinX.serialization.core)
            runtimeOnly(LOGBACK)
            runtimeOnly(JCL_OVER_SLF4J)
            runtimeOnly(LOG4J_OVER_SLF4J)
            if (hasTests) {
                testImplementation(DOKT_TEST)
                testImplementation(Testing.kotest.assertions.core)
                testImplementation(Testing.kotest.framework.api)
                testImplementation(KotlinX.serialization.json)
                testRuntimeOnly(LOGBACK)
                testRuntimeOnly(Testing.kotest.runner.junit5)
                testRuntimeOnly(JCL_OVER_SLF4J)
                testRuntimeOnly(LOG4J_OVER_SLF4J)
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

    private fun DependencyHandler.api(dep: Any) = add("api", dep)

    private fun DependencyHandler.implementation(dep: Any) = add("implementation", dep)

    private fun DependencyHandler.runtimeOnly(dep: Any) = add("runtimeOnly", dep)

    private fun DependencyHandler.testImplementation(dep: Any) = add("testImplementation", dep)

    private fun DependencyHandler.testRuntimeOnly(dep: Any) = add("testRuntimeOnly", dep)
}
