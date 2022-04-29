package app.dokt.generator.application

import app.dokt.generator.domain.BoundedContext

interface Application {
    /** Conflicts org.gradle.api.Project.description */
    val appDescription: String?

    /** Conflicts org.gradle.api.Project.displayName */
    val appDisplayName: String?

    /** Conflicts org.gradle.api.Project.name */
    val appName: String

    val boundedContexts: List<BoundedContext>

    val domainSources: String

    val generated: String

    val generatedSources: String

    val generatedTestSources: String

    val group: String?
}
