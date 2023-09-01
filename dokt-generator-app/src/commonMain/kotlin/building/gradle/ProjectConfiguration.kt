package app.dokt.generator.building.gradle

data class ProjectConfiguration(
    /** Use cross-project dependencies. */
    val cross: Boolean = false,

    /** Use local Maven repository. */
    var local: Boolean = false
)
