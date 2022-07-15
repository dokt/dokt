package app.dokt.generator.domain

import app.dokt.app.*
import app.dokt.domain.event.RootEvent
import app.dokt.domain.test.*
import app.dokt.generator.alsoIf
import app.dokt.generator.code.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import kotlinx.serialization.KSerializer

val BuildingBlock.asClassName get() = ClassName(module, name)

class KotlinPoetAggregateCommandCoder(command: AggregateCommand, private val toType: TypeName)
    : AggregateCommandCoder<FunSpec>(command) {
    companion object {
        private val testParam = ParameterSpec.builder("test",
            LambdaTypeName.suspending(FunSpecContainerScope::class.asTypeName())
        ).build()
    }

    private val parameters = values.asParameters

    override fun codeCommands() = FunSpec.abstract(methodName, parameters)

    override fun codeService() = FunSpec.suspendingBuilder(methodName)
        .addParameter("to", toType)
        .addParameters(parameters)
        .returns("tx(to) { $methodCall }")

    override fun codeSpec() = FunSpec.builder(methodName)
        .addParameter(testParam)
        .returns("context(%S, test)", methodName)

    override fun codeTestAggregate() = FunSpec.overrideBuilder(methodName, parameters)
        //.addKdoc("$name command")
        .returns("command.$methodCall")
}

class KotlinPoetAggregateEventCoder(event: AggregateEvent, private val eventType: TypeName)
    : AggregateEventCoder<FunSpec, TypeSpec>(event) {
    companion object {
        private val serializableType = kotlinx.serialization.Serializable::class.asTypeName()
    }

    private val type = event.asClassName
    private val parameters = details.asParameters

    init {
        // TODO Add support for "root"-named detail.
        if (details.any { it.name == "root" }) throw UnsupportedOperationException("Event detail name can't be 'root'!")
    }

    override fun code() = when (details.size) {
        0 -> TypeSpec.objectBuilder(name)
        1 -> TypeSpec.valueClassBuilder(name, details.single())
        else -> TypeSpec.dataClassBuilder(name, details)
    }.addAnnotation(serializableType).addSuperinterface(eventType).build()

    override fun codeAggregate() = FunSpec.overrideBuilder(methodName, parameters)
        .addStatement("root.$methodCall")
        .addStatement("add(%T$constructorCall)", type)
        .build()

    fun codeApplyCase(apply: FunSpec.Builder) = apply.addStatement("is %T -> root.$methodCall", type)

    override fun codeTestAggregate() = FunSpec.overrideBuilder(methodName, parameters)
        //.addKdoc("$name event")
        .returns("apply(%T$constructorCall) { $methodCall }", type)
}

class KotlinPoetAggregateCoder(
    root: AggregateRoot,
    main: GeneratedSources = GeneratedSources(),
    test: GeneratedSources = GeneratedSources(true)
) : AggregateCoder<FileSpec, FunSpec, PropertySpec, TypeSpec>(root, main, test) {
    companion object {
        private val aggregateType = Aggregate::class.asTypeName()
        private val applicationServiceType = ApplicationService::class.asTypeName()
        private val arrangerType = Arranger::class.asTypeName()
        private val funSpecType = io.kotest.core.spec.style.FunSpec::class.asTypeName()
        private val genericActorType = Actor::class.asTypeName()
        private val genericTestAggregateType = TestAggregate::class.asTypeName()
        private val genericToType = To::class.asTypeName()
        private val rootEventType = RootEvent::class.asTypeName()
        private val serializerType = KSerializer::class.asTypeName()
        private val uuidType = uuidRef.asClassName
        private val uuid4Member = MemberName("com.benasher44.uuid", "uuid4")
    }

    private val commandsType = application("Commands")
    val eventType = domain("Event")
    private val eventsType = root.eventsInterface.asClassName
    val idType = rootId?.asClassName ?: uuidType
    val rootType = domain()
    private val specType = domain("Spec")
    private val testAggregateType = application("TestAggregate")
    private val toType = genericToType.parameterizedBy(idType)

    private val actorType = genericActorType.parameterizedBy(commandsType, rootType, eventType)

    private fun application(suffix: String) = ClassName(application, "$name$suffix")

    override fun codeAggregate() = TypeSpec.classBuilder(name + "Aggregate")
        .primaryConstructor(id.asConstructor)
        .superclass(aggregateType.parameterizedBy(rootType, idType, eventType))
        .addSuperclassConstructorParameter(id.name)
        .addSuperinterface(eventsType)
        .addFunction(FunSpec.overrideBuilder("apply")
            .receiver(eventType)
            .beginReturn("when(this)")
            .also { apply -> eventCoders.forEach { (it as KotlinPoetAggregateEventCoder).codeApplyCase(apply) } }
            .endControlFlow()
            .build()
        )
        .addFunction(FunSpec.overrideBuilder("create")
            .returns("$name${construct("id")}.also { it.emit = this }"))
        .addFunctions(eventCoders.map { it.codeAggregate() })
        .build()

    override fun codeApplication(types: List<TypeSpec>) =
        FileSpec.builder(application, name + "Application").addTypes(types).build()

    override fun codeApplicationTest(property: PropertySpec, types: List<TypeSpec>) =
        FileSpec.builder(application, name + "ApplicationTest").addProperty(property).addTypes(types).build()

    override fun codeCommands() = TypeSpec.interfaceBuilder(name + "Commands")
        .addFunctions(commandCoders.map { it.codeCommands() })
        .build()

    override fun codeDomain(types: List<TypeSpec>) =
        FileSpec.builder(domain, name + "Events").addTypes(types).build()

    override fun codeDomainTest(type: TypeSpec) = FileSpec.get(domain, type)

    override fun codeEvent() = TypeSpec.sealedInterfaceBuilder(eventType).addSuperinterface(rootEventType).build()

    override fun codeSerializer() = PropertySpec
        .privateBuilder("serializer", serializerType.parameterizedBy(rootType))
        .initializer("%T.serializer()", rootType)
        .build()

    override fun codeService() = TypeSpec.objectBuilder("${name}Service")
        .superclass(applicationServiceType.parameterizedBy(rootType, idType, eventType))
        .addSuperclassConstructorParameter("%T::class", rootType)
        .addFunctions(commandCoders.map { it.codeService() })
        .build()

    override fun codeSpec() = TypeSpec.abstractClassBuilder(specType)
        .primaryConstructor(FunSpec.constructorBuilder("body", LambdaTypeName.of(specType))
            .alsoIf (rootHasId) {
                addParameter(
                    ParameterSpec.builder(testIdName, idType)
                        .also {
                            // ID default test value generation
                            if (uuid) it.defaultValue("%M()", uuid4Member)
                            else if (id.isString()) it.defaultValue("%S", testIdName)
                        }
                        .build()
                )
            }
            .build())
        .alsoIf (rootHasId) {
            addProperty(PropertySpec.privateInitialized(testIdName, idType))
        }
        .superclass(funSpecType)
        .addInitializerBlock(CodeBlock.of("body()"))
        .addProperty(PropertySpec.builder(memberName, actorType)
            .initializer("%T(%T(%T${construct(testIdName)}))",
                genericActorType, testAggregateType, rootType)
            .build()
        )
        .addFunction(FunSpec.builder(memberName)
            .alsoIf (rootHasId) {
                addParameter(ParameterSpec.builder(id.name, idType).defaultValue(testIdName).build())
            }
            .addParameter(ParameterSpec.builder("apply", LambdaTypeName.get(eventsType, nullable = true))
                .defaultValue("null").build())
            .returns("%T<%T, %T, %T, %T>(%T(%T${construct()}))(apply)",
                arrangerType, commandsType, rootType, eventsType, eventType, testAggregateType, rootType))
        .addFunctions(commandCoders.map { it.codeSpec() })
        .build()

    override fun codeTestAggregate() = TypeSpec.classBuilder(name + "TestAggregate")
        .primaryConstructor("root", rootType)
        .superclass(genericTestAggregateType.parameterizedBy(rootType, eventsType, eventType))
        .addSuperclassConstructorParameter("root, serializer")
        .addSuperinterfaces(commandsType, eventsType)
        .addFunctions(commandCoders.map { it.codeTestAggregate() })
        .addFunctions(eventCoders.map { it.codeTestAggregate() })
        .build()

    private fun domain(suffix: String = "") = ClassName(domain, "$name$suffix")

    override fun AggregateCommand.toCoder() = KotlinPoetAggregateCommandCoder(this, toType)

    override fun AggregateEvent.toCoder() = KotlinPoetAggregateEventCoder(this, eventType)

    override fun FileSpec.toCodeFile() = KotlinPoetFile(this)
}

class KotlinPoetBoundedContextCoder(
    boundedContext: BoundedContext,
    main: GeneratedSources = GeneratedSources(),
    test: GeneratedSources = GeneratedSources(true)
) : BoundedContextCoder<FileSpec, TypeSpec>(boundedContext, main, test) {
    override val aggregateCoders =
        model.aggregateRoots.map { KotlinPoetAggregateCoder(it, generatedMain, generatedTest) }

    override fun DomainServiceInterface.codeTestImplementation() = when {
        isCollection -> TypeSpec.objectBuilder(name + "InMem")
            .addSuperinterface(
                MUTABLE_LIST.parameterizedBy(implements.first().argument.asClassName),
                CodeBlock.of("mutableListOf()")
            )
            .addSuperinterface(asClassName).build()
        else -> throw UnsupportedOperationException()
    }

    override fun FileSpec.toCodeFile() = KotlinPoetFile(this)
}
