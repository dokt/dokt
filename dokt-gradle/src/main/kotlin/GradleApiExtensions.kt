package app.dokt.gradle

import org.gradle.api.*

val Project.commonMainDir get() = projectDir.resolve("src").resolve("commonMain")

val Project.isRootProject get() = equals(rootProject)

val Project.multiplatform get() = commonMainDir.exists()
