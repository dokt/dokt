package app.dokt.gradle.core

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.reflect.KClass

abstract class DoktJvmPlugin(type: KClass<out DoktJvmPlugin>) :
    DoktSinglePlugin<KotlinJvmProjectExtension>(type, "jvm", KotlinJvmProjectExtension::class)
{
    private val mainJava: Path by lazy { main.java }
    private val mainJavaExists by lazy { mainJava.exists() }

    override fun KotlinJvmProjectExtension.configureKotlin() {
        // tasks.withType<Test>().configureEach { useJUnitPlatform() }
        /*if (testExists) project.tasks.withType<Test> {
            ignoreFailures = true
            useJUnitPlatform()
        }*/
    }


    override fun NamedDomainObjectContainer<KotlinSourceSet>.configureSourceSets() {
        configure(main) {
            dependencies {
                configureMainDependencies()
            }
            configureMain()
        }
        configure(test) {
            dependencies {
                jvmTestDependencies()
                configureTestDependencies()
            }
            configureTest()
        }
    }
}
