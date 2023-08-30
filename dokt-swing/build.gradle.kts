@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    `maven-publish`
    signing
}

setDoktDefaults("0.2.11-SNAPSHOT", "Dokt Swing API.")

dependencies {
    api(project(":dokt-interface"))
    api("com.github.jiconfont:jiconfont-google_material_design_icons:2.2.0.2")
    api("com.github.jiconfont:jiconfont-font_awesome:4.7.0.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3") // TODO Implement
    implementation("com.github.jiconfont:jiconfont-swing:1.0.1")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.11")
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
