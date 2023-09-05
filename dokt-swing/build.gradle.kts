@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

description = "Dokt Swing API."

dependencies {
    api(project(":dokt-interface"))
    api(libs.jiconfont.google.material.design.icons)
    api(libs.jiconfont.font.awesome)
    api(libs.kotlinx.coroutines.swing) // TODO Implement
    implementation(libs.jiconfont.swing)
    runtimeOnly(libs.logback.classic)
}

java {
    configureJava()
}

kotlin {
    configureKotlin()
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
