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
     * Update build.gradle.kts file `false` (default) or replaced it `true`. This configuration has no effect if
     * configurations are stored in-memory.
     */
    abstract val replaceBuildFile: Property<Boolean>

    /**
     * When `true` adds local Maven to repositories. Defaults to `false`.
     */
    abstract val useMavenLocal: Property<Boolean>

    /**
     * When `true` the generated dependencies uses *refreshVersions* dependency notations e.g.
     * `implementation(Kotlin.stdlib.jdk8)`. Enabled by default if `de.fayard.refreshVersions` plugin is applied.
     * When `false` uses Gradle dependency notation e.g. `implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")`
     * **Notice:** underscore as a version because plugins doesn't know it!
     */
    abstract val useRefreshVersionsNotation: Property<Boolean>

    /**
     * When `true` the generated dependencies uses Kotlin dependency extensions e.g.
     * `implementation(kotlin("stdlib-jdk8"))`. When `false` the behaviour is defined by [useRefreshVersionsNotation].
     */
    abstract val useKotlinExtensions: Property<Boolean>
}
