package app.dokt.generator.building

import app.dokt.common.*

/** Architecture layer */
enum class Layer(
    /** Must be multiplatform */
    val multiplatform: Boolean,

    vararg tags: String
) {
    /**
     * Application layer targets to multiplatform.
     *
     * - Handles authorization before service call.
     * - Handles transactions.
     * - Has application services which are common to all interfaces.
     * - May have application service infrastructure for other platforms.
     */
    APPLICATION(true, "app"),

    /**
     * Domain layer targets to multiplatform.
     *
     * - Domain logic is written only using Common Kotlin code.
     * - Domain service infrastructure may be implemented in other platforms.
     */
    DOMAIN(true, "dom"),

    /** Infrastructure project which may contain application or domain service implementations to any platform. */
    INFRASTRUCTURE(false, "dokt", "inf"),

    /**
     * Interface application layer.
     *
     * - Handles authentication.
     * - Contains runnable interface application and its implementation.
     * - Dispatches calls to application services.
     */
    INTERFACE(false, "api", "if", "int", "ktor", "swing", "swt", "ui");

    val en = "${name.capitalize} architecture layer"

    val tags = tags.toSet()

    override fun toString() = en

    companion object {
        private val tagMap = values().flatMap { layer -> layer.tags.map { it to layer } }.toMap()

        fun parse(names: List<String>) = tagMap.firstValueByKey(INFRASTRUCTURE) {
                tag -> names.any { it.startsWith(tag) }
        }
    }
}