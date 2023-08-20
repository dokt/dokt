package app.dokt.generator.building

import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

/** Dokt library */
enum class Dokt {
    APPLICATION,
    COMMON,
    DOMAIN,
    DOMAIN_TEST,
    INTERFACE,
    TEST;

    val artifact = "dokt-${name.toLowerCaseAsciiOnly().replace('_', '-')}"

    companion object {
        /** Use local project copies (workaround for KTIJ-22057). */
        const val LOCAL = false
    }
}