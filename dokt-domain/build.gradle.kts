plugins {
    `maven-publish`
    signing
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

description = "Dokt Domain API for defining domain layer and its infrastructure."

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                api("com.benasher44:uuid:_") // Generated Aggregate may need.
                api(KotlinX.datetime)
                api(KotlinX.serialization.core)
            }
        }
    }
}
