package app.dokt.gradle.building

import org.gradle.api.Incubating
import org.gradle.api.provider.Property

/** Extension to configure Dokt settings plugin. */
abstract class DoktSettingsExtension : DoktExtension() {
    /**
     * When set to `true`, Gradle uses *only* the settings file to initialize the settings. Default is `false`, in which
     * case Dokt adds its own initializations in addition to the settings file.
     */
    abstract val useOnlySettingsFile: Property<Boolean>

    /**
     * Configures the cross-project dependency resolution aspects `true` or configure them per project `false`
     * (default).
     */
    @get:Incubating
    abstract val useCrossProjectDependencies: Property<Boolean>
}
