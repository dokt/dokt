package app.dokt.gradle

import KotlinPlatform.*
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

open class KotlinProject<E>(project: Project, protected val extension: E) : Project by project {
    private val commonMain by lazy { src.resolve(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME) }

    protected val commonMainKotlin by lazy { commonMain.resolve(KOTLIN) }

    private val commonTest by lazy { src.resolve(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME) }

    protected val generatedDir by lazy { buildDir.resolve("generated") }

    protected val generatedCommonMain by lazy { generatedDir.resolve(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME) }

    protected val generatedCommonTest by lazy { generatedDir.resolve(KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME) }

    private val main by lazy { src.resolve("main") }

    private val mainKotlin by lazy { main.resolve(KOTLIN) }

    /** Is root project */
    val isRoot by lazy { project == rootProject }

    val jvmMain by lazy { src.resolve("jvmMain") }

    val jvmTest by lazy { src.resolve("jvmTest") }

    private val src by lazy { projectDir.resolve("src") }

    private val test by lazy { src.resolve("test") }

    //val testKotlin by lazy { test.resolve(KOTLIN) }

    val platform = when {
        name.endsWith("-js") -> JS
        name.endsWith("-jvm") || mainKotlin.exists() -> JVM
        commonMain.exists() -> MULTI
        else -> null
    }

    val hasTests = if (platform == MULTI) commonTest.exists() else test.exists()

    fun cleanGenerated() { project.delete(generatedDir) }

    companion object {
        private const val KOTLIN = "kotlin"
    }
}
