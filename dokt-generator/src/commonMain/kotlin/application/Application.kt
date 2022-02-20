package app.dokt.generator.application

import app.dokt.generator.domain.BoundedContext

interface Application {
    val boundedContexts: List<BoundedContext>

    val description: String?

    val displayName: String?

    val domainSources: String

    val generated: String

    val generatedSources: String

    val generatedTestSources: String

    val group: String?

    val name: String
}
