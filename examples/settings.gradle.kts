@file:Suppress("SpellCheckingInspection")
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}
plugins {
  id("app.dokt") version "0.2.10"
  id("de.fayard.refreshVersions") version "0.51.0"
}
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}
rootProject.name = "examples"

// Domain architecture layer projects
include("erp-dom", "file-dom", "hash-dom", "hello-dom", "window-dom")

// Application architecture layer projects
include("window-simulator-app")

// Interface architecture layer projects
include("window-simulator-swing")
