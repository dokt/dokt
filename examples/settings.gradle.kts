@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenLocal()
  }
}
plugins {
  id("app.dokt") version "0.2.11-SNAPSHOT"
  id("de.fayard.refreshVersions") version "0.60.1"
}
dokt {
    useOnlyBuildFile = true
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
