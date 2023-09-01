package app.dokt.gradle.building

import org.gradle.api.provider.Property

/** Extension to configure Dokt project or settings plugin. */
abstract class DoktExtension {
    /**
     * When set to `true`, Gradle uses *only* the build file to configure the project. Default is `false`, in which case
     * Dokt adds its own configurations in addition to the build file.
     */
    abstract val useOnlyBuildFile: Property<Boolean>

    /**
     * When `true` adds local Maven to repositories. Defaults to `false`.
     */
    abstract val useMavenLocal: Property<Boolean>
}
