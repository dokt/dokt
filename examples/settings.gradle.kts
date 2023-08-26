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

dokt {
  useBuildFile.set(true)
}

rootProject.name = "examples"
