package app.dokt.generator.domain

import app.dokt.Root
import app.dokt.generator.code.*
import app.dokt.generator.pluralize
import kotlin.jvm.JvmStatic

abstract class BoundedContextReader {
    protected val log = mu.KotlinLogging.logger {}

    companion object {
        private val AggregateRootName = app.dokt.AggregateRoot::class.qualifiedName
        private val RootName = Root::class.qualifiedName

        protected val TypeDef.extendsRoot get() = extends?.qualifiedName == RootName
        protected val TypeDef.implementsRoot get() = rootInterface != null

        @JvmStatic
        protected val TypeDef.rootInterface get() = implements.firstOrNull { it.qualifiedName == AggregateRootName }
    }

    protected abstract fun readContext(rootDef: TypeDef, other: List<TypeDef>): BoundedContext

    /**
     * Read bounded contexts which needs code generation.
     */
    fun readContexts(sources: Sources): List<BoundedContext> {
        log.trace { "Searching aggregate roots from ${sources.basePath}..." }
        val roots = sources.types.filter { it.extendsRoot || it.implementsRoot }
        log.info { "Found ${"aggregate root".pluralize(roots)}." }
        return roots.map{ root ->
            // TODO Ensure that aggregate root is serializable.
            val other = sources.getPackageTypes(root.packageName) - root
            log.info { "Reading $root bounded context from ${"type".pluralize(other)}..." }
            val context = readContext(root, other)
            log.info { "Read $context bounded context." }
            context
        }
    }
}
