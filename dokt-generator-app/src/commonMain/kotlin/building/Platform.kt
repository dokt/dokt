package app.dokt.generator.building

import app.dokt.common.firstValueByKey

/**
 * Kotlin target platform
 */
enum class Platform(val id: String, en: String, val main: String = "main", val test: String = "test") {
    /** JavaScript platform */
    JS("js", "JavaScript "),

    /** Java Virtual Machine platform */
    JVM("jvm", "Java Virtual Machine "),

    /** Multiplatform */
    MULTI("multiplatform", "Multi", "commonMain", "commonTest");

    val en = "${en}platform"

    val isMulti get() = this == MULTI

    override fun toString() = en

    companion object {
        private val byId = values().associateBy { it.id }

        @Deprecated("Use ProjectType and parse(path)")
        fun parse(layer: Layer, names: List<String>) = when {
            layer.multiplatform -> MULTI
            layer == Layer.INTERFACE && (names.contains("swing") || names.contains("swt")) -> JVM
            else -> byId.firstValueByKey(MULTI) { names.contains(it) }
        }

        fun parse(path: String) = when {
            path.contains(JVM.id) -> JVM
            path.contains(JS.id) -> JS
            else -> MULTI
        }
    }
}