@file:Suppress("unused_variable")
val kotlinGroup = "org.jetbrains.kotlin"
val kotlinx = "${kotlinGroup}x:kotlinx"
plugins {
    `maven-publish`
    signing
    id("com.github.ben-manes.versions") version "0.42.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}
val vGradle: String by project
val vKotest: String by project
val vSerialization: String by project
val String.kotest get() = "io.kotest:kotest-$this:$vKotest"
fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kotestApi() =
    setOf("assertions-core", "framework-api").forEach { api(it.kotest) }

allprojects {
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    group = "app.dokt"
    version = "0.2.0-SNAPSHOT"
    description = "Domain-driven design using Kotlin"

    repositories {
        mavenCentral()
    }

    if (name != "dokt-gradle") {
        setOf("multiplatform", "plugin.serialization").forEach { apply(plugin = "$kotlinGroup.$it") }

        kotlin {
            jvm {
                testRuns["test"].executionTask.configure { useJUnitPlatform() }
            }

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        when (project.name) {
                            "dokt" -> {
                                api("com.benasher44:uuid:0.4.0")
                                setOf("coroutines-core:1.6.0", "datetime:0.3.2", "serialization-core:$vSerialization")
                                    .forEach { api("$kotlinx-$it") }
                            }
                            "dokt-test" -> {
                                api(project(":"))
                                kotestApi()
                                implementation("$kotlinx-serialization-json:1.3.2")
                            }
                            "dokt-generator" -> {
                                implementation(project(":dokt-test"))
                                implementation("io.github.microutils:kotlin-logging:2.1.21")
                            }
                        }
                    }
                }
                val commonTest by getting {
                    dependencies {
                        kotestApi()
                        runtimeOnly("runner-junit5".kotest)
                    }
                }
                if (project.name == "dokt-generator") {
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
    }

    publishing {
        publications.withType<MavenPublication> {
            pom {
                name.set(project.name.split('-')
                    .joinToString(" ") { it[0].toUpperCase() + it.substring(1) })
                description.set(project.description)
                url.set("https://dokt.app/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("papinkivi")
                        name.set("Jukka Papinkivi")
                        email.set("jukka@papinkivi.fi")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/dokt/dokt.git")
                    developerConnection.set("scm:git:ssh://github.com:dokt/dokt.git")
                    url.set("https://github.com/dokt/dokt")
                }
            }
        }
    }

    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = vGradle
}
