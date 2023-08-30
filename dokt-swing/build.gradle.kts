@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt Swing API.")

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

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
