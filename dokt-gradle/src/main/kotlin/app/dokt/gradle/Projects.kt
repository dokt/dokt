package app.dokt.gradle

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val Project.commonMainDir get() = buildDir.resolve(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
val Project.commonTestDir get() = buildDir.resolve(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME)