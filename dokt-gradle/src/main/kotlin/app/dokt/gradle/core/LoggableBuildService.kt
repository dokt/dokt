package app.dokt.gradle.core

import org.gradle.api.services.*
import kotlin.reflect.KClass

abstract class LoggableBuildService<T : BuildServiceParameters>(type: KClass<out LoggableBuildService<T>>) :
    BuildService<T>, Loggable by LoggableWrapper(type)