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
            api(Ktor.server)
            api(Ktor.server.cachingHeaders)
            api(Ktor.server.callLogging)
            api(Ktor.server.contentNegotiation)
            api(Ktor.server.htmlBuilder)
            api(Ktor.server.hostCommon) // for shutdown URL
            api(Ktor.server.resources)
            api(Ktor.plugins.serialization.kotlinx.json)
        }

        jvmMainDependencies {
            api(Ktor.server.netty)
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
