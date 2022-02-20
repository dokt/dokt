@file:Suppress("unused_variable")
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.42.0"
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

val vKotest: String by project
val vSerialization: String by project
val kotlinx = "org.jetbrains.kotlinx:kotlinx"
val String.kotest get() = "io.kotest:kotest-$this:$vKotest"
fun org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension.jvmWithJunit() =
    jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
fun KotlinDependencyHandler.kotest() {
    kotestApi()
    kotestRunner()
}
fun KotlinDependencyHandler.kotestApi() =
    listOf("assertions-core", "framework-api").forEach { api(it.kotest) }
fun KotlinDependencyHandler.kotestRunner() = runtimeOnly("runner-junit5".kotest)
fun Project.applyKotlin(module: String) = apply(plugin = "org.jetbrains.kotlin.$module")
fun Project.mavenPublish() {
    apply<MavenPublishPlugin>()
    publishing { publications { create<MavenPublication>("maven") { from(components["kotlin"]) } } }
}

allprojects {
    group = "app.dokt"
    version = "0.2.0-SNAPSHOT"
    repositories { mavenCentral() }
}

mavenPublish()
kotlin {
    jvmWithJunit()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.benasher44:uuid:0.4.0")
                setOf("coroutines-core:1.6.0", "datetime:0.3.2", "serialization-core:$vSerialization").map {
                    api("$kotlinx-$it")
                }
            }
        }
        val commonTest by getting { dependencies { kotest() } }
    }
}
tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.4"
}

project("dokt-test") {
    applyKotlin("multiplatform")
    applyKotlin("plugin.serialization")
    mavenPublish()
    kotlin {
        jvmWithJunit()
        sourceSets {
            val commonMain by getting {
                dependencies {
                    api(project(":"))
                    kotestApi()
                    implementation("$kotlinx-serialization-json:1.3.2")
                }
            }
            val commonTest by getting { dependencies { kotestRunner() } }
        }
    }
}

project("dokt-generator") {
    applyKotlin("multiplatform")
    mavenPublish()
    kotlin {
        jvmWithJunit()
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(project(":dokt-test"))
                    kotestApi()
                    implementation("io.github.microutils:kotlin-logging:2.1.21")
                }
            }
            val commonTest by getting { dependencies { kotest() } }
            val jvmMain by getting {
                dependencies {
                    implementation("com.squareup:kotlinpoet:1.10.2")
                    implementation(kotlin("compiler-embeddable"))
                    runtimeOnly("ch.qos.logback:logback-classic:1.2.10")
                }
            }
        }
    }
}
