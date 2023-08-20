plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
}
rootProject.name = "dokt"
include(
    "dokt-test",
    "dokt-common",
    "dokt-infrastructure",
    "dokt-domain",
    "dokt-domain-test",
    "dokt-application",
    "dokt-interface",
    "dokt-generator-app",
    "dokt-gradle",
    "dokt-ktor-server",
)
