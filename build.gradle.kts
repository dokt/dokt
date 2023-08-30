@file:Suppress("SpellCheckingInspection")

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.github.gradle-nexus.publish-plugin")
    id("io.gitlab.arturbosch.detekt")
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
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
