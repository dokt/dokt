pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal() // Needed only for testing unreleased Dokt plugin versions
    }
}
rootProject.name = "examples"
include("erp", "hello")