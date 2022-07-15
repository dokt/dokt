@file:Suppress("UNUSED_VARIABLE")

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
}

description = "Dokt User Interface API for defining presentation layer infrastructure."

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":dokt-application"))
            }
        }

        val jvmMain by getting {
            dependencies {
                // Exclude these if not creating Swing app.
                api("com.github.jiconfont:jiconfont-google_material_design_icons:_")
                api("com.github.jiconfont:jiconfont-font_awesome:_")
                //api(KotlinX.coroutines.swing) TODO ?
                implementation(project(":dokt-common"))
                implementation("com.github.jiconfont:jiconfont-swing:_")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(Testing.kotest.runner.junit5)
                runtimeOnly("ch.qos.logback:logback-classic:_")
            }
        }
    }
}
