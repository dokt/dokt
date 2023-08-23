package app.dokt.gradle

import app.dokt.common.lowerFirst
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

val Project.commonMainDir get() = buildDir.resolve(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)
val Project.commonTestDir get() = buildDir.resolve(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME)

inline fun <reified T : Task> TaskContainer.register(): TaskProvider<T> = T::class.run {
    register(simpleName!!.lowerFirst, java)
}