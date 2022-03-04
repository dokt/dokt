package app.dokt.gradle

import org.gradle.api.*
import java.io.File

val Project.commonMainDir: File get() = file("src/commonMain")

val Project.isRootProject get() = equals(rootProject)

val Project.multiplatform get() = commonMainDir.exists()
