package app.dokt.generator.building.gradle


data class SettingsInitialization(
    /** Use cross-project dependencies. */
    val cross: Boolean = false,

    /** Projects use local Maven repository. */
    var local: Boolean = false,

    /** Root project name. */
    var root: String? = null,

    /** Subprojects to include */
    val include: List<String> = emptyList()
)
