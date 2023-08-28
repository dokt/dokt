package app.dokt.gradle.building

import org.gradle.api.provider.Property

/** Extension to configure Dokt settings plugin. */
abstract class DoktSettingsExtension : DoktExtension() {
    /**
     * Configures the cross-project dependency resolution aspects `true` or configure them per project `false` (default).
     */
    abstract val useCrossProjectDependencies: Property<Boolean>
}
