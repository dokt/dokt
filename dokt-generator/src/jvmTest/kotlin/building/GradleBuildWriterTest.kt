package app.dokt.generator.building

import app.dokt.generator.code.*
import app.dokt.generator.kotlinpoet.shouldCode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldHaveSize

class GradleBuildWriterTest : FunSpec({
    context("generateScript") {
        test("examples") {
            generateScript() shouldCode """
                plugins { id("app.dokt") }
                tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }
                tasks.create("copyDokt") {
                  doLast {
                    copy {
                      from("..")
                      include("dokt-*/**")
                      exclude("dokt-g*", "*/build", "*/src/*Test")
                      into(projectDir)
                    }
                  }
                }
            """
        }

        test("erp-dom") {
            generateScript("erp-dom") shouldCode """
                plugins {
                  kotlin("multiplatform")
                  kotlin("plugin.serialization")
                  id("app.dokt.domain")
                }
                repositories {
                  mavenCentral()
                }
                kotlin {
                  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
                  sourceSets {
                    val commonMain by getting {
                      dependencies {
                        implementation(project(":dokt-application"))
                        implementation(project(":dokt-domain"))
                        implementation(KotlinX.serialization.core)
                      }
                      kotlin.srcDir(buildDir.resolve("commonMain"))
                    }
                    val commonTest by getting {
                      dependencies {
                        implementation(project(":dokt-domain-test"))
                        implementation(KotlinX.serialization.json)
                        implementation("io.kotest:kotest-runner-junit5-jvm:_")
                      }
                      kotlin.srcDir(buildDir.resolve("commonTest"))
                    }
                  }
                }
            """
        }

        test("hello-dom") {
            generateScript("hello-dom") shouldCode """
                plugins {
                  kotlin("multiplatform")
                  kotlin("plugin.serialization")
                  id("app.dokt.domain")
                }
                repositories {
                  mavenCentral()
                }
                kotlin {
                  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
                  sourceSets {
                    val commonMain by getting {
                      dependencies {
                        implementation(project(":dokt-application"))
                        implementation(project(":dokt-domain"))
                        implementation(KotlinX.serialization.core)
                      }
                      kotlin.srcDir(buildDir.resolve("commonMain"))
                    }
                    val commonTest by getting {
                      dependencies {
                        implementation(project(":dokt-domain-test"))
                        implementation(KotlinX.serialization.json)
                        implementation("io.kotest:kotest-runner-junit5-jvm:_")
                      }
                      kotlin.srcDir(buildDir.resolve("commonTest"))
                    }
                  }
                }
            """
        }

        test("window-simulator-app") {
            generateScript("window-simulator-app") shouldCode """
                plugins {
                  kotlin("multiplatform")
                }
                repositories {
                  mavenCentral()
                }
                kotlin {
                  jvm()
                  sourceSets["commonMain"].dependencies {
                    implementation(project(":dokt-application"))
                  }
                  sourceSets["jvmMain"].dependencies {
                    implementation("net.java.dev.jna:jna-platform:_")
                  }
                }
            """
        }

        test("window-simulator-swing") {
            generateScript("window-simulator-swing") shouldCode """
                plugins {
                  application
                  kotlin("jvm")
                }
                repositories {
                  mavenCentral()
                }
                dependencies {
                  implementation(project(":dokt-interface"))
                  implementation(project(":window-simulator-app"))
                }
                application { mainClass.set("fi.papinkivi.simulator.WindowSimulatorSwingKt") }
            """
        }

        xtest("descendants") {
            instance.project.descendants.forEach {
                println(it.path)
                GradleBuildWriter(it).generateScript().print()
            }
        }
    }

    xtest("print") {
        val kotlinFile: KotlinFile? = null // GradleBuild(Path.of("..")).kotlinFile!!
        kotlinFile!!.imports shouldHaveSize 1
        val ktFile = kotlinFile.element
        ktFile.println(System.out)
    }

    test("write") {
        instance.write()
    }

}) {
    companion object {
        val instance = GradleBuildWriter(GradleProjectTest.instance)

        private fun generateScript(path: String = ":") =
            GradleBuildWriter(GradleProjectTest.instance[path]).generateScript()
    }
}