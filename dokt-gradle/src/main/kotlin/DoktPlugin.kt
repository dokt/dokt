package app.dokt.gradle

import app.dokt.generator.application.*
import org.gradle.api.*
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import java.util.*

@Suppress("unused", "unused_variable")
class DoktPlugin : Plugin<Project> {
    companion object {
        const val DOKT = "app.dokt:dokt"
        const val GENERATE_CODE = "generateCode"
    }

    private lateinit var application: GradleApplication
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    private val properties = javaClass.getResourceAsStream("/dokt.properties")
        .use { input -> Properties().also { it.load(input) }}

    private val vDokt: String by properties
    private val vGradle: String by properties
    private val vKotest: String by properties
    private val vSerialization: String by properties

    override fun apply(project: Project) {
        application = GradleApplication(project)
        if (project.multiplatform) project.applyDokt()
        project.childProjects.values.forEach { it.pluginManager.apply(DoktPlugin::class.java) }
    }

    private fun Project.applyDokt() {
        pluginManager.apply(KotlinMultiplatformPluginWrapper::class.java)
        pluginManager.apply(SerializationGradleSubplugin::class.java)

        repositories {
            mavenCentral()
            if (vDokt.contains("SNAPSHOT")) mavenLocal()
        }

        configure<KotlinMultiplatformExtension> {
            jvm {
                testRuns["test"].executionTask.configure {
                    useJUnitPlatform()
                }
            }

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        implementation("$DOKT:$vDokt")
                    }
                    kotlin.srcDir(application.generatedSources)
                }

                val commonTest by getting {
                    dependencies {
                        implementation("$DOKT-test:$vDokt")
                        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$vSerialization")
                        runtimeOnly("io.kotest:kotest-runner-junit5:$vKotest")
                    }
                    kotlin.srcDir(application.generatedTestSources)
                }
            }
        }

        task("cleanGenerated") {
            doLast { application.cleanGenerated() }
        }.description = "Delete generated files."

        task(GENERATE_CODE) {
            doLast { coder.code() }
        }.description = "Generate application classes."

        task("generateDocumentation") {
            doLast { documentWriter.document() }
        }.description = "Generate Markdown documentation of the application."

        tasks {
            filter { it.name.startsWith("compileKotlin") }.forEach { it.dependsOn(GENERATE_CODE) }

            findByName("wrapper")?.let { (it as Wrapper).gradleVersion = vGradle }
        }
    }
}
