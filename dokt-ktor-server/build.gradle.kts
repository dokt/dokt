@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt Ktor server API")

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-interface"))
            api("io.ktor:ktor-server:2.3.3")
            api("io.ktor:ktor-server-caching-headers:2.3.3")
            api("io.ktor:ktor-server-call-logging:2.3.3")
            api("io.ktor:ktor-server-content-negotiation:2.3.3")
            api("io.ktor:ktor-server-host-common:2.3.3") // for shutdown URL
            api("io.ktor:ktor-server-html-builder:2.3.3")
            api("io.ktor:ktor-server-resources:2.3.3")
            api("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
        }

        jvmMainDependencies {
            api("io.ktor:ktor-server-netty:2.3.3")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
