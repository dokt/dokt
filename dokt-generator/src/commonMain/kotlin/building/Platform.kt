package app.dokt.generator.building

import app.dokt.common.firstValueByKey

/**
 * Kotlin target platform
 */
enum class Platform(val id: String, en: String, val main: String = "main", val test: String = "test") {
    /** Multiplatform */
    MULTI("multiplatform", "Multi", "commonMain", "commonTest"),

    /** JavaScript platform */
    JS("js", "JavaScript "),

    /** Java Virtual Machine platform */
    JVM("jvm", "Java Virtual Machine ");

    val en = "${en}platform"

    val isMulti get() = this == MULTI

    override fun toString() = en

    companion object {
        private val byId = values().associateBy { it.id }

        fun parse(layer: Layer, names: List<String>) = when {
            layer.multiplatform -> MULTI
            layer == Layer.INTERFACE && (names.contains("swing") || names.contains("swt")) -> JVM
            else -> byId.firstValueByKey(MULTI) { names.contains(it) }
        }
    }
}