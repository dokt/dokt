pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}
plugins {
  id("de.fayard.refreshVersions") version "0.40.2"
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
