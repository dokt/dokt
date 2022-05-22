@file:Suppress("unused_variable")

import org.jetbrains.kotlin.gradle.plugin.*
//import org.jetbrains.dokka.gradle.*

plugins {
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
    //id("org.jetbrains.dokka")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

//#region Functions
fun kmp(path: String, desc: String, dep: Action<NamedDomainObjectContainer<KotlinSourceSet>>) = project(path) {
    description = "$desc for Domain-driven design using Kotlin."

    //apply<DokkaPlugin>()
    apply<KotlinMultiplatformPluginWrapper>()
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    kotlin {
        jvm { testRuns["test"].executionTask.configure { useJUnitPlatform() } }
        sourceSets(dep)
    }
}
fun NamedDomainObjectContainer<KotlinSourceSet>.commonDoktTest() =
    commonTest { implementation(project(":dokt-test")) }
fun NamedDomainObjectContainer<KotlinSourceSet>.commonMain(configure: KotlinDependencyHandler.() -> Unit) {
    val commonMain by getting { dependencies { configure() } }
}
fun NamedDomainObjectContainer<KotlinSourceSet>.commonTest(configure: KotlinDependencyHandler.() -> Unit) {
    val commonTest by getting { dependencies { configure() } }
}
fun NamedDomainObjectContainer<KotlinSourceSet>.jvmMain(configure: KotlinDependencyHandler.() -> Unit) {
    val jvmMain by getting { dependencies { configure() } }
}
//#endregion

allprojects {
    group = "app.dokt"
    version = "0.3.0-SNAPSHOT"

    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

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

kmp(":", "Common (logging free) language utilities") {
    commonMain {
        api("com.benasher44:uuid:_")
        api(KotlinX.coroutines.core)
        api(KotlinX.datetime)
        api(KotlinX.serialization.core)
    }
    commonDoktTest()
    jvmMain {
        api(kotlin("stdlib-jdk8"))
    }
}

kmp("dokt-test", "Common (logging free) test utilities") {
    commonMain {
        api(Testing.kotest.assertions.core)
        api(Testing.kotest.framework.datatest)
        api(Testing.kotest.runner.junit5)
        api(KotlinX.serialization.json)
    }
}

kmp("dokt-domain", "Domain (logging free) support classes") {
    commonMain { api(project(":")) }
    commonDoktTest()
}

kmp("dokt-domain-test", "Domain (logging free) test classes") {
    commonMain {
        api(project(":dokt-domain"))
        api(project(":dokt-test"))
    }
}

kmp("dokt-application", "Application support classes") {
    commonMain {
        api(project(":"))
        api("io.github.microutils:kotlin-logging:_")
    }
    commonDoktTest()
    jvmMain {
        api("net.java.dev.jna:jna-platform:_")
    }
}

kmp("dokt-interface", "Interface support classes") {
    commonMain { api(project(":dokt-application")) }
    commonDoktTest()
    jvmMain {
        // Exclude these if not creating Swing app.
        api("com.github.jiconfont:jiconfont-google_material_design_icons:_")
        api("com.github.jiconfont:jiconfont-font_awesome:_")
        api("org.jfree:jfreechart:_")
        api(KotlinX.coroutines.swing)
        implementation("com.github.jiconfont:jiconfont-swing:_")
    }
}

kmp("dokt-generator", "Code and documentation generator") {
    commonMain {
        implementation(project(":dokt-domain-test"))
        implementation("io.github.microutils:kotlin-logging:_")
    }
    commonDoktTest()
    jvmMain {
        implementation(Square.kotlinPoet)
        implementation(kotlin("compiler-embeddable"))// TODO `1.5.31` that might work differently than in the requested version `1.6.21`
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

tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }
