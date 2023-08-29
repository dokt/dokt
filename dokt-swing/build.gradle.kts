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
    api("com.github.jiconfont:jiconfont-google_material_design_icons:_")
    api("com.github.jiconfont:jiconfont-font_awesome:_")
    //api(KotlinX.coroutines.swing) TODO ?
    implementation("com.github.jiconfont:jiconfont-swing:_")
    runtimeOnly("ch.qos.logback:logback-classic:_")
}

kotlin {
    configureKotlin()
}

detekt {
    configureDetekt(config)
}

publishing(createDoktPublication(description!!))

signing(configureDoktSigning(publishing))
