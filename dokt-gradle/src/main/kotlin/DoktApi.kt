@file:Suppress("unused")

import org.gradle.api.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask

val Project.isRoot get() = path == ":"

// https://stackoverflow.com/questions/68830854/how-to-execute-a-node-command-in-a-kotlin-js-project
private val Project.nodeDir get() = (rootProject.tasks.getByPath("kotlinNodeJsSetup") as NodeJsSetupTask).destination

val Project.npm get() = nodeDir.resolve("npm.cmd")

val Project.npx get() = nodeDir.resolve("npx.cmd")
