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
            """
        }

        test("erp-dom") {
            generateScript("erp-dom") shouldCode """
                plugins {
                  kotlin("multiplatform")
                  kotlin("plugin.serialization")
                  id("app.dokt.domain")
                }
                kotlin {
                  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
                  sourceSets {
                    val commonMain by getting {
                      dependencies {
                        implementation("app.dokt:dokt-application:_")
                      }
                      kotlin.srcDir(buildDir.resolve("commonMain"))
                    }
                    val commonTest by getting {
                      dependencies {
                        implementation("app.dokt:dokt-domain-test:_")
                        implementation(Testing.kotest.runner.junit5)
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
                kotlin {
                  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
                  sourceSets {
                    val commonMain by getting {
                      dependencies {
                        implementation("app.dokt:dokt-application:_")
                      }
                      kotlin.srcDir(buildDir.resolve("commonMain"))
                    }
                    val commonTest by getting {
                      dependencies {
                        implementation("app.dokt:dokt-domain-test:_")
                        implementation(Testing.kotest.runner.junit5)
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
                kotlin {
                  jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
                  sourceSets["commonMain"].dependencies {
                    implementation("app.dokt:dokt-application:_")
                  }
                  sourceSets["jvmMain"].dependencies {
                    implementation("net.java.dev.jna:jna-platform:_")
                  }
                  sourceSets["jvmTest"].dependencies {
                    implementation("app.dokt:dokt-test:_")
                    implementation(Testing.kotest.runner.junit5)
                    runtimeOnly("ch.qos.logback:logback-classic:_")
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
                dependencies {
                  implementation("app.dokt:dokt-interface:_")
                  implementation(project(":window-simulator-app"))
                  runtimeOnly("ch.qos.logback:logback-classic:_")
                }
                application { mainClass.set("fi.papinkivi.simulator.WindowSimulatorSwingKt") }
            """
        }

        xtest("descendants") {
            instance.project.descendants.forEach {
                println(it.path)
                GradleBuildWriter(it).createModel().print()
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
            GradleBuildWriter(GradleProjectTest.instance[path]).createModel().build()
    }
}