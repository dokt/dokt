@file:Suppress("SpellCheckingInspection")

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("io.gitlab.arturbosch.detekt:detekt-api:1.23.1")
}
