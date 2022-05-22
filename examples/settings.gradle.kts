pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal() // Needed only for testing unreleased Dokt plugin versions
    }
}
plugins {
    id("app.dokt") version "0.3.0-SNAPSHOT"
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
rootProject.name = "examples"
include(
    //#region Domains
    "erp-dom",
    "hello-dom",
    //#endregion

    //#region Applications
    "window-simulator-app",
    //#endregion

    //#region Interfaces
    "jvm",
    "jvm:window-simulator-if",
    //#endregion
)
