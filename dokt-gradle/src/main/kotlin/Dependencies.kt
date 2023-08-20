import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

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