@file:Suppress("unused")

import org.gradle.api.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import java.nio.file.Path

private fun KotlinMultiplatformExtension.dependencies(
    sourceSet: String, configure: KotlinDependencyHandler.() -> Unit) = sourceSets.getByName(sourceSet) {
    it.dependencies(configure)
} as KotlinSourceSet

fun KotlinMultiplatformExtension.commonMainDependencies(configure: KotlinDependencyHandler.() -> Unit) =
    dependencies("commonMain", configure)

fun KotlinMultiplatformExtension.commonTestDependencies(configure: KotlinDependencyHandler.() -> Unit) =
    dependencies("commonTest", configure)

fun KotlinMultiplatformExtension.jvmMainDependencies(configure: KotlinDependencyHandler.() -> Unit) =
    dependencies("jvmMain", configure)

fun KotlinMultiplatformExtension.jvmTestDependencies(configure: KotlinDependencyHandler.() -> Unit) =
    dependencies("jvmTest", configure)

val Project.isRoot get() = path == ":"

// https://stackoverflow.com/questions/68830854/how-to-execute-a-node-command-in-a-kotlin-js-project
private val Project.nodeDir get() = (rootProject.tasks.getByPath("kotlinNodeJsSetup") as NodeJsSetupTask).destination

val Project.npm get() = nodeDir.resolve("npm.cmd")

val Project.npx get() = nodeDir.resolve("npx.cmd")

fun Project.src(): Path = projectDir.resolve("src").toPath()

fun Project.task(name: String, description: String, action: (Task) -> Unit): Task = task(name) {
    it.group = group.toString()
    it.description = description
    it.actions.add(action)
}
