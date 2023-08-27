package app.dokt.gradle.core

import org.gradle.api.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.*
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.reflect.KClass

abstract class DoktMultiPlugin(type: KClass<out DoktMultiPlugin>) :
    DoktKotlinPlugin<KotlinMultiplatformExtension>(
        type,
        "multiplatform",
        KotlinMultiplatformExtension::class
    )
{
    private val commonMain by lazy { src(COMMON_MAIN) }
    private val commonMainExists by lazy { commonMain.exists() }
    private val commonTest by lazy { src(COMMON_TEST) }
    private val commonTestExists by lazy { commonTest.exists() }
    private val jvmMain by lazy { src(JVM_MAIN) }
    private val jvmMainExists by lazy { jvmMain.exists() }
    private val jvmMainJava: Path by lazy { jvmMain.java }
    private val jvmMainJavaExists by lazy { jvmMainJava.exists() }
    private val jvmTest by lazy { src(JVM_TEST) }
    private val jvmTestExists by lazy { jvmTest.exists() }
    private val jsMain by lazy { src(JS_MAIN) }
    private val jsMainExists by lazy { jsMain.exists() }
    private val jsTest by lazy { src(JS_TEST) }
    override val platformName = kotlinModule

    final override fun Project.validate() {
        require(commonMainExists || jvmMainExists || jsMainExists) {
            "Require Common, JVM or JS sources to detect target platform for $path project!" }
    }

    override fun KotlinMultiplatformExtension.configureKotlin() {
        if (jvmMainExists || (commonMainExists && !jsMainExists)) {
            jvm {
                if (jvmMainJavaExists) withJava()
                if (commonTestExists || jvmTestExists) testRuns.getByName("test").executionTask.configure {
                    it.ignoreFailures = true
                    it.useJUnitPlatform()
                }
            }
        }
    }

    override fun NamedDomainObjectContainer<KotlinSourceSet>.configureSourceSets() {
        configure(commonMain) {
            dependencies {
                configureCommonMainDependencies()
            }
            configureCommonMain()
        }
        configure(commonTest) {
            dependencies {
                jvmTestDependencies()
                configureCommonTestDependencies()
            }
            configureCommonTest()
        }
        configure(jvmMain) {
            dependencies {
                jvmLoggingDependencies()
                configureJvmMainDependencies()
            }
        }
        configure(jvmTest) {
            dependencies {
                jvmTestDependencies()
                configureJvmTestDependencies()
            }
        }
        configure(jsMain) {
            dependencies {
                configureJsMainDependencies()
            }
        }
        configure(jsTest) {
            dependencies {
                configureJsTestDependencies()
            }
        }
    }

    protected open fun KotlinDependencyHandler.configureCommonMainDependencies() = noOtherDependencies(COMMON_MAIN)

    protected open fun KotlinDependencyHandler.configureCommonTestDependencies() = noOtherDependencies(COMMON_TEST)

    protected open fun KotlinDependencyHandler.configureJvmMainDependencies() = noOtherDependencies(JVM_MAIN)

    protected open fun KotlinDependencyHandler.configureJvmTestDependencies() = noOtherDependencies(JVM_TEST)

    protected open fun KotlinDependencyHandler.configureJsMainDependencies() = noOtherDependencies(JS_MAIN)

    protected open fun KotlinDependencyHandler.configureJsTestDependencies() = noOtherDependencies(JS_TEST)

    protected open fun KotlinSourceSet.configureCommonMain() = noOtherConfigs(COMMON_MAIN)

    protected open fun KotlinSourceSet.configureCommonTest() = noOtherConfigs(COMMON_TEST)

    companion object {
        const val COMMON_MAIN = "commonMain"
        const val COMMON_TEST = "commonTest"
        const val JVM_MAIN = "jvmMain"
        const val JVM_TEST = "jvmTest"
        const val JS_MAIN = "jsMain"
        const val JS_TEST = "jsTest"
    }
}
