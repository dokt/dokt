package app.dokt.generator.building

enum class ProjectType(val platform: Platform? = null, val layer: Layer? = null) {
    APPLICATION(Platform.MULTI, Layer.APPLICATION,),
    DOMAIN(Platform.MULTI, Layer.DOMAIN),
    DOMAINS,
    INFRASTRUCTURE(Platform.MULTI, Layer.INFRASTRUCTURE),
    INFRASTRUCTURE_JS(Platform.JS, Layer.INFRASTRUCTURE),
    INFRASTRUCTURE_JVM(Platform.JVM, Layer.INFRASTRUCTURE),
    INTERFACE(Platform.MULTI, Layer.INTERFACE),
    INTERFACE_JS(Platform.JS, Layer.INTERFACE),
    INTERFACE_JVM(Platform.JVM, Layer.INTERFACE),
    KTOR_SERVER(Platform.JVM, Layer.INTERFACE),
    ROOT,
    SWING(Platform.JVM, Layer.INTERFACE),
    SWT(Platform.JVM, Layer.INTERFACE);

    companion object {
        private val ignoredNames = setOf("build", "gradle", "src")
        private val ignoredNamePrefixes = setOf("doc", "x")

        fun ignore(name: String) = ignoredNames.contains(name) || ignoredNamePrefixes.any { name.startsWith(it) }

        /** Detect project type. See [Flowchart](doc/project-detection.graphml). */
        fun parse(path: String) = parse(path, path.substringAfterLast(':'))

        fun parse(path: String, name: String) = when {
            path == ":" -> ROOT
            ignore(name) -> null
            path.contains("test") -> parseInfrastructure(path)
            name.startsWith("dom") -> DOMAINS // Do not parse subprojects. They are domains.
            name.contains("-dom") -> DOMAIN
            name.contains("-app") -> APPLICATION
            name.contains("-if") || name.contains("-int") || name.contains("-itf") ->
                when (Platform.parse(path)) {
                    Platform.JVM -> INTERFACE_JVM
                    Platform.JS -> INTERFACE_JS
                    Platform.MULTI -> INTERFACE
                }
            name.endsWith("-api") || name.contains("ktor-s") -> KTOR_SERVER
            name.endsWith("-swing") -> SWING
            name.endsWith("-swt") -> SWT
            else -> parseInfrastructure(path)
        }

        private fun parseInfrastructure(path: String) = when (Platform.parse(path)) {
            Platform.JVM -> INFRASTRUCTURE_JVM
            Platform.JS -> INFRASTRUCTURE_JS
            Platform.MULTI -> INFRASTRUCTURE
        }
    }
}