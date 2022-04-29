package app.dokt.gradle

import KotlinPlatform.*
import KotlinX
import app.dokt.generator.application.*
import de.fayard.refreshVersions.RefreshVersionsPlugin
import org.gradle.api.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin

/**
 * Dokt settings and project plugin.
 * See https://tomgregory.com/gradle-evaluation-order-for-multi-project-builds/
 */
@Suppress("unused", "unused_variable")
class DoktPlugin : Plugin<Any> {
    private lateinit var project: DoktProject
    private val coder by lazy { KotlinPoetApplicationCoder(project) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(project) }

    override fun apply(target: Any) {
        if (target is Project) {
            project = DoktProject(target)
            project.configure()
        } else if (target is Settings) {
            target.initialize()
        }
    }

    private fun DoktProject.configure() {
        allprojects {
            repositories {
                mavenCentral()
                mavenLocal()
            }
        }

        when (platform) {
            JS -> configureJs()
            JVM -> configureJvm()
            MULTI -> configureMultiplatform()
        }

        if (isRoot) {
            tasks.named<Wrapper>("wrapper") {
                distributionType = Wrapper.DistributionType.ALL
            }
        }

        subprojects {
            apply<DoktPlugin>()
        }
    }

    private fun DoktProject.configureJs() {
        apply<KotlinJsPluginWrapper>()
        applyCommonPlugins()
    }

    private fun DoktProject.configureJvm() {
        apply<KotlinPluginWrapper>()
        applyCommonPlugins()

        if (hasTests) {
            configureTests()

            dependencies {
                implementation(KOTLIN_LOGGING)
                implementation(JUL_TO_SLF4J)
                runtimeOnly(LOGBACK)
                runtimeOnly(JCL_OVER_SLF4J)
                runtimeOnly(LOG4J_OVER_SLF4J)
                testImplementation(Testing.kotest.assertions.core)
                testImplementation(Testing.kotest.framework.api)
                testRuntimeOnly(LOGBACK)
                testRuntimeOnly(Testing.kotest.runner.junit5)
                testRuntimeOnly(JCL_OVER_SLF4J)
                testRuntimeOnly(LOG4J_OVER_SLF4J)
            }
        }
    }

    private fun DoktProject.configureMultiplatform() {
        apply<KotlinMultiplatformPluginWrapper>()
        applyCommonPlugins()

        configure<KotlinMultiplatformExtension> {
            jvm {
                if (hasTests) configureTests()
            }

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        implementation(KOTLIN_LOGGING)
                        if (isDomainLayer) implementation("$DOKT:_")
                    }
                    if (isDomainLayer) kotlin.srcDir(generatedSources)
                }

                if (hasTests) {
                    val commonTest by getting {
                        dependencies {
                            if (isDomainLayer) {
                                implementation("$DOKT-test:_")
                                implementation(KotlinX.serialization.json)
                            }
                            implementation(Testing.kotest.assertions.core)
                            implementation(Testing.kotest.framework.api)
                            runtimeOnly(LOGBACK)
                            runtimeOnly(Testing.kotest.runner.junit5)
                            runtimeOnly(JCL_OVER_SLF4J)
                            runtimeOnly(LOG4J_OVER_SLF4J)
                        }
                        if (isDomainLayer) kotlin.srcDir(generatedTestSources)
                    }
                }

                if (jvmMain.exists()) {
                    val jvmMain by getting {
                        dependencies {
                            implementation(JUL_TO_SLF4J)
                            runtimeOnly(LOGBACK)
                        }
                    }
                }

                if (jvmTest.exists()) {
                    val jvmMain by getting {
                        dependencies {
                            runtimeOnly(LOGBACK)
                        }
                    }
                }
            }
        }

        if (isDomainLayer) {
            task("cleanGenerated") {
                doLast { cleanGenerated() }
            }.description = "Delete generated files."

            task(GENERATE_CODE) {
                doLast { coder.code() }
            }.description = "Generate application classes."

            task("generateDocumentation") {
                doLast { documentWriter.document() }
            }.description = "Generate Markdown documentation of the application."

            tasks.filter { it.name.startsWith("compileKotlin") }.forEach { it.dependsOn(GENERATE_CODE) }
        }
    }

    private fun Settings.initialize() {
        println("Initializing dokt.app on Gradle ${gradle.gradleVersion}.")
        pluginManagement {
            repositories {
                gradlePluginPortal()
                mavenLocal()
            }
        }
        apply<RefreshVersionsPlugin>()
    }

    companion object {
        private const val DOKT = "app.dokt:dokt"
        private const val GENERATE_CODE = "generateCode"
        private const val JUL_TO_SLF4J = "org.slf4j:jul-to-slf4j:_"
        private const val JCL_OVER_SLF4J = "org.slf4j:jcl-over-slf4j:_"
        private const val LOGBACK = "ch.qos.logback:logback-classic:_"
        private const val LOG4J_OVER_SLF4J = "org.slf4j:log4j-over-slf4j:_"
        private const val KOTLIN_LOGGING = "io.github.microutils:kotlin-logging:_"

        private fun DependencyHandler.implementation(dependencyNotation: Any) =
            add("implementation", dependencyNotation)

        private fun DependencyHandler.runtimeOnly(dependencyNotation: Any) =
            add("runtimeOnly", dependencyNotation)

        private fun DependencyHandler.testImplementation(dependencyNotation: Any) =
            add("testImplementation", dependencyNotation)

        private fun DependencyHandler.testRuntimeOnly(dependencyNotation: Any) =
            add("testRuntimeOnly", dependencyNotation)

        private fun Project.applyCommonPlugins() {
            apply<SerializationGradleSubplugin>()
            apply<MavenPublishPlugin>()
        }

        /** Was previously: testRuns["test"].executionTask.configure { useJUnitPlatform() } */
        private fun Project.configureTests() = tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }
}
