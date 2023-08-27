@file:Suppress("SpellCheckingInspection")

plugins {
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
    id("io.gitlab.arturbosch.detekt")
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
}

repositories { // detekt requires repositories
    mavenCentral()
}

detekt {
    config.setFrom(file("config/detekt/detekt.yml"))
}

subprojects {
    group = "app.dokt"
    version = "0.2.10"

    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            //allWarningsAsErrors = true
            jvmTarget = "1.8"
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
            md.required.set(false)
        }
    }

    /* Works only for multiplatform
    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }*/

    //val dokkaHtml by tasks.getting(DokkaTask::class)

    /*if (name != "dokt-gradle") { TODO
        val javadocJar by tasks.registering(Jar::class) {
            //dependsOn(dokkaHtml)
            archiveClassifier.set("javadoc")
            //from(dokkaHtml.outputDirectory)
        }
    }*/

    publishing {
        repositories {
            maven("https://pkgs.dev.azure.com/papinkivi/_packaging/common/maven/v1") {
                name = "papinkivi"
                authentication {
                    create<BasicAuthentication>("basic")
                }
                credentials {
                    username = "papinkivi"
                    // read from user.home\.gradle\gradle.properties
                    val papinkiviPersonalAccessToken: String? by project
                    password = papinkiviPersonalAccessToken
                }
            }
        }

        publications.withType<MavenPublication> {
            //artifact(javadocJar.get()) TODO
            pom {
                // TODO Handle root name
                name.set(project.name.split('-')
                    .joinToString(" ") { it[0].uppercaseChar() + it.substring(1) })
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
        // read from user.home\.gradle\gradle.properties
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

// https://issues.sonatype.org/browse/OSSRH-78373
nexusPublishing {
    repositories {
        sonatype()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.2.1"
}
