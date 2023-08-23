package app.dokt.gradle

import app.dokt.gradle.core.ProjectPlugin
import kotlin.reflect.KClass

abstract class DoktMultiplatformPlugin(type: KClass<out DoktMultiplatformPlugin>) : ProjectPlugin(type) {
}