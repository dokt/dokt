@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt common logging free language utilities.")

kotlin {
    configureJvmWithTests()

    sourceSets {
        commonMainDependencies {
            api("com.benasher44:uuid:_")
            api(KotlinX.datetime)
            api(KotlinX.serialization.core)
        }

        commonTestDependencies {
            implementation(project(":dokt-test"))
            implementation(Testing.kotest.runner.junit5)
        }

        jvmMainDependencies {
            api("org.apache.commons:commons-lang3:_")
        }
    }
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication("Dokt common utils"))

signing(configureDoktSigning(publishing))
