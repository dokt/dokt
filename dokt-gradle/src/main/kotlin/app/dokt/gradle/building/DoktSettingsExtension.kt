package app.dokt.gradle.building

import org.gradle.api.Incubating
import org.gradle.api.provider.Property

/** Extension to configure Dokt settings plugin. */
abstract class DoktSettingsExtension : DoktExtension() {
    /**
     * Default is `false` and then minimal initialization is done (a build service is registered and a plugin is applied
     * on the root project). When set to `true`, then full initialization is done (also manages cross-project
     * dependencies, searches subprojects, includes them in the build and applies a plugin for each subproject).
     */
    abstract val inMemoryInitialization: Property<Boolean>

    /**
     * Configures the cross-project dependency resolution aspects `true` or configure them per project `false`
     * (default).
     */
    @get:Incubating
    abstract val useCrossProjectDependencies: Property<Boolean>
}
