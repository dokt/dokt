@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt Ktor server API")

kotlin {
    configureJvm()

    sourceSets {
        commonMainDependencies {
            api(project(":dokt-interface"))
            api(libs.ktor.server)
            api(libs.ktor.server.caching.headers)
            api(libs.ktor.server.call.logging)
            api(libs.ktor.server.content.negotiation)
            api(libs.ktor.server.host.common) // for shutdown URL
            api(libs.ktor.server.html.builder)
            api(libs.ktor.server.resources)
            api(libs.ktor.serialization.kotlinx.json)
        }

        jvmMainDependencies {
            api(libs.ktor.server.netty)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
