import org.gradle.api.provider.Property

/**
 * Optional Dokt configuration
 */
interface DoktExtension {
    val layer: Property<Layer>
    val platform: Property<KotlinPlatform>
    val mavenPublish: Property<Boolean>
}

/** Kotlin platform */
enum class KotlinPlatform {
    JS,
    JVM,
    MULTI
}

/**
 * (Onion) architecture layer.
 *
 * Logging can be done in any layer. See:
 * https://exchangetuts.com/where-logging-should-go-in-onion-architecture-with-ddd-1640078703507858
 */
enum class Layer(
    /** Project name suffixes to identify layer if it isn't defined. */
    vararg suffixes: String
) {
    /** Application layer */
    APPLICATION("app"),

    /**
     * Domain model layer which contain bounded contexts.
     *
     *
     **/
    DOMAIN("bc", "ctx", "cxt", "dom"),

    /**
     * Infrastructure layer
     *
     * Abbreviations https://www.abbreviations.com/abbreviation/infrastructure:
     * - INFRA
     * - ISX
     **/
    INFRASTRUCTURE("inf", "isx"),

    /**
     * Presentation or interface layer
     *
     * Abbreviations https://www.allacronyms.com/interface/abbreviated:
     * - I/F
     * - IF
     * - intf
     * - ITF
     * - INTFC
     **/
    INTERFACE("api", "if", "int", "itf", "swing", "swt");

    val abbreviations = suffixes.toList()
    val suffixes = suffixes.map { "-$it" }
}
