plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.40.1"
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
rootProject.name = "dokt"
include(
    "dokt-test",
    "dokt-domain",
    "dokt-domain-test",
    "dokt-application",
    "dokt-interface",
    "dokt-generator",
    "dokt-gradle"
)
