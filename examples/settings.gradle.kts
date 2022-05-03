pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal() // Needed only for testing unreleased Dokt plugin versions
    }
}
plugins {
    id("app.dokt") version "0.3.0-SNAPSHOT"
}

rootProject.name = "examples"
include("erp-dom", "hello-dom")
