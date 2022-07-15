pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}
plugins {
  id("de.fayard.refreshVersions") version "0.40.2"
}
rootProject.name = "examples"

// Dokt libraries
include("dokt-application", "dokt-common", "dokt-domain", "dokt-domain-test", "dokt-interface",
    "dokt-test")

// Domain architecture layer projects
include("erp-dom", "file-dom", "hash-dom", "hello-dom")

// Application architecture layer projects
include("window-simulator-app")

// Interface architecture layer projects
include("window-simulator-swing")
