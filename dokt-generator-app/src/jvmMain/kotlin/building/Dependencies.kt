package app.dokt.generator.building

import app.dokt.common.lowerFirst

enum class Configuration {
    Api,
    Implementation,
    RuntimeOnly,
    TestImplementation,
    TestRuntimeOnly;

    override fun toString() = name.lowerFirst
}

data class Dependency(
    val expression: String,
    val packagePrefix: String = "",
    val target: Target = Target.JVM,
    val configuration: Configuration = Configuration.Implementation
)

enum class Target {
    COMMON,
    JS,
    JVM
}
