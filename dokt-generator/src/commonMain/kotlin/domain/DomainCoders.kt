package app.dokt.generator.domain

import app.dokt.generator.code.*
import app.dokt.generator.*

/**
 * Generates support and test classes for given aggregate root to application layer.
 */
abstract class AggregateCoder<F, M, P, T>(
    func: () -> Unit,
    root: AggregateRoot,
    main: GeneratedSources = GeneratedSources(),
    test: GeneratedSources = GeneratedSources(true)
) : AbstractCoder<F, AggregateRoot>(func, root, main, test) {
    protected val application = "${root.module}.app"
    protected val commandCoders by lazy { root.commands.map { it.toCoder() } }
    protected val domain = root.module
    protected val eventCoders by lazy { root.events.map { it.toCoder() } }
    protected val rootId = root.id
    protected val rootHasId = rootId != null
    protected val id = rootId ?: defaultIdVar
    private val idName = id.name
    protected val infrastructure = "${root.module}.infra"
    protected val uuid = id.type == uuidRef
    protected val rootName = root.name
    protected val memberName = rootName.lowerFirst()
    protected val testIdName = "test${id.name.upperFirst()}"

    override fun code() {
        generatedMain.add(codeDomain())
        generatedTest.add(codeDomainTest())
        generatedMain.add(codeApplication())
        generatedTest.add(codeApplicationTest())
    }

    abstract fun codeAggregate(): T

    fun codeApplication() = codeApplication(listOf(codeAggregate(), codeService()))

    abstract fun codeApplication(types: List<T>): F

    fun codeApplicationTest() = codeApplicationTest(codeSerializer(), listOf(codeCommands(), codeTestAggregate()))

    abstract fun codeApplicationTest(property: P, types: List<T>): F

    abstract fun codeCommands(): T

    fun codeDomain() = codeDomain(listOf(codeEvent()) + eventCoders.map { it.code() })

    abstract fun codeDomain(types: List<T>): F

    fun codeDomainTest() = codeDomainTest(codeSpec())

    abstract fun codeDomainTest(type: T): F

    abstract fun codeEvent(): T

    abstract fun codeSerializer(): P

    abstract fun codeService(): T

    abstract fun codeSpec(): T

    abstract fun codeTestAggregate(): T

    protected fun construct(idParam: String = idName) = if (rootHasId) "($idParam)" else "()"

    protected abstract fun AggregateCommand.toCoder(): AggregateCommandCoder<M>

    protected abstract fun AggregateEvent.toCoder(): AggregateEventCoder<M, T>
}

abstract class AggregateCommandCoder<M>(command: AggregateCommand) {
    protected val methodName = command.methodName
    protected val values = command.values
    protected val methodCall = "$methodName(${values.names})"
    protected val name = command.name

    abstract fun codeCommands(): M

    abstract fun codeService(): M

    abstract fun codeSpec(): M

    abstract fun codeTestAggregate(): M
}

abstract class AggregateEventCoder<M, T>(event: AggregateEvent) {
    protected val details = event.details
    protected val methodName = event.method.name
    protected val name = event.name
    private val parameterNames = "(${details.names})"
    protected val methodCall = "${methodName}$parameterNames"
    protected val constructorCall = if (details.isEmpty()) "" else parameterNames

    abstract fun code(): T

    abstract fun codeAggregate(): M

    abstract fun codeTestAggregate(): M
}

abstract class BoundedContextCoder<F, T>(
    func: () -> Unit,
    boundedContext: BoundedContext,
    main: GeneratedSources = GeneratedSources(),
    test: GeneratedSources = GeneratedSources(true)
) : AbstractCoder<F, BoundedContext>(func, boundedContext, main, test) {
    abstract val aggregateCoders: List<AggregateCoder<*, *, *, *>>

    override fun code() {
        aggregateCoders.forEach {
            info { "Coding $it aggregate" }
            it.code()
        }
    }

    abstract fun DomainServiceInterface.codeTestImplementation(): T
}
