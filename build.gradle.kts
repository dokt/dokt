@file:Suppress("SpellCheckingInspection")

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
}

mavenCentral() // Detekt requires

tasks.register("detektAll") {
    group = "verification"
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}

subprojects {
    //val dokkaHtml by tasks.getting(DokkaTask::class)

    /*if (name != "dokt-gradle") { TODO
        val javadocJar by tasks.registering(Jar::class) {
            //dependsOn(dokkaHtml)
            archiveClassifier.set("javadoc")
            //from(dokkaHtml.outputDirectory)
        }
    }*/
}

// https://issues.sonatype.org/browse/OSSRH-78373
nexusPublishing {
    repositories {
        sonatype()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.3"
}
