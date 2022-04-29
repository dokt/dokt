@file:Suppress("unused_variable")

//import org.jetbrains.dokka.gradle.*

plugins {
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
    //id("org.jetbrains.dokka")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

allprojects {
    //apply<DokkaPlugin>()
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    group = "app.dokt"
    version = "0.3.0-SNAPSHOT"
    description = "Domain-driven design using Kotlin"

    repositories {
        mavenCentral()
    }

    if (name != "dokt-gradle") {
        apply(plugin = "org.jetbrains.kotlin.multiplatform")
        apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

        kotlin {
            jvm {
                // TODO
                testRuns["test"].executionTask.configure { useJUnitPlatform() }
            }

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        when (project.name) {
                            "dokt" -> {
                                api("com.benasher44:uuid:_")
                                api(KotlinX.coroutines.core)
                                api(KotlinX.datetime)
                                api(KotlinX.serialization.core)
                            }
                            "dokt-test" -> {
                                implementation(project(":"))
                                api(project(":"))
                                api(Testing.kotest.assertions.core)
                                api(Testing.kotest.framework.api)
                                implementation(KotlinX.serialization.json)
                            }
                            "dokt-generator" -> {
                                implementation(project(":dokt-test"))
                                implementation("io.github.microutils:kotlin-logging:_")
                            }
                        }
                    }
                }
                val commonTest by getting {
                    dependencies {
                        api(Testing.kotest.assertions.core)
                        api(Testing.kotest.framework.api)
                        runtimeOnly(Testing.kotest.runner.junit5)
                    }
                }
                if (project.name == "dokt-generator") {
                    val jvmMain by getting {
                        dependencies {
                            implementation(Square.kotlinPoet)
                            implementation(kotlin("compiler-embeddable"))
                            runtimeOnly("ch.qos.logback:logback-classic:_")
                        }
                    }
                }
            }
        }
    }

    //val dokkaHtml by tasks.getting(DokkaTask::class)

    val javadocJar by tasks.registering(Jar::class) {
        //dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        //from(dokkaHtml.outputDirectory)
    }

    publishing {
        publications.withType<MavenPublication> {
            artifact(javadocJar.get())
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
}
