package app.dokt.gradle.core


import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.*
import kotlin.io.path.exists
import kotlin.reflect.KClass

/** Single target platform plugin */
abstract class DoktSinglePlugin<E : KotlinSingleTargetExtension<*>>(
    type: KClass<out DoktSinglePlugin<E>>, kotlinModule: String, kotlinProjectExtensionClass: KClass<E>) :
    DoktKotlinPlugin<E>(type, kotlinModule, kotlinProjectExtensionClass)
{
    protected val main by lazy { src(MAIN) }
    protected val mainExists by lazy { main.exists() }
    protected val test by lazy { src(TEST) }
    protected val testExists by lazy { test.exists() }
    final override val platformName = kotlinModule.uppercase()

    protected open fun KotlinDependencyHandler.configureMainDependencies() = noOtherDependencies(MAIN)

    protected open fun KotlinDependencyHandler.configureTestDependencies() = noOtherDependencies(TEST)

    protected open fun KotlinSourceSet.configureMain() = noOtherConfigs(MAIN)

    protected open fun KotlinSourceSet.configureTest() = noOtherConfigs(TEST)

    companion object {
        const val MAIN = "main"
        const val TEST = "test"

        //#region Configurations
        private const val API = "api"
        private const val IMPLEMENTATION = "implementation"
        private const val RUNTIME_ONLY = "runtimeOnly"
        private const val TEST_IMPLEMENTATION = "testImplementation"
        private const val TEST_RUNTIME_ONLY = "testRuntimeOnly"
        //#endregion

    }
}