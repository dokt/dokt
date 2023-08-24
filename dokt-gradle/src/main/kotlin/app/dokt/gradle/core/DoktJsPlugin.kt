package app.dokt.gradle.core

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import kotlin.reflect.KClass

abstract class DoktJsPlugin(type: KClass<out DoktJsPlugin>) :
    DoktSinglePlugin<KotlinJsProjectExtension>(type, "js", KotlinJsProjectExtension::class)
{
    override fun KotlinJsProjectExtension.configureKotlin() {
        TODO()
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
                //jvmTestDependencies()
                configureTestDependencies()
            }
            configureTest()
        }
    }
}