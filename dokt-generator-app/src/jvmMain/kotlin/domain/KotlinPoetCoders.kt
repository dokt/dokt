package app.dokt.generator.domain

import app.dokt.app.Aggregate
import app.dokt.app.AggregateApplicationService
import app.dokt.app.To
import app.dokt.common.alsoIf
import app.dokt.domain.event.RootEvent
import app.dokt.domain.test.Actor
import app.dokt.domain.test.Arranger
import app.dokt.domain.test.TestAggregate
import app.dokt.generator.code.GeneratedSources
import app.dokt.generator.code.KotlinPoetFile
import app.dokt.generator.code.abstract
import app.dokt.generator.code.abstractClassBuilder
import app.dokt.generator.code.addSuperinterfaces
import app.dokt.generator.code.addTypes
import app.dokt.generator.code.asClassName
import app.dokt.generator.code.asConstructor
import app.dokt.generator.code.asParameters
import app.dokt.generator.code.beginReturn
import app.dokt.generator.code.constructorBuilder
import app.dokt.generator.code.dataClassBuilder
import app.dokt.generator.code.get
import app.dokt.generator.code.initialized
import app.dokt.generator.code.of
import app.dokt.generator.code.overrideBuilder
import app.dokt.generator.code.primaryConstructor
import app.dokt.generator.code.privateBuilder
import app.dokt.generator.code.returns
import app.dokt.generator.code.sealedInterfaceBuilder
import app.dokt.generator.code.suspending
import app.dokt.generator.code.suspendingBuilder
import app.dokt.generator.code.valueClassBuilder
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MUTABLE_LIST
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import kotlinx.serialization.KSerializer

val SUPPRESS_UNUSED = AnnotationSpec.builder(Suppress::class).addMember("%S", "unused").build()
val SUPPRESS_UNUSED_PRIVATE = AnnotationSpec.builder(Suppress::class)
    .addMember("%S, %S", "unused", "MemberVisibilityCanBePrivate").build()

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

    // TODO Handle multiple parameters and their default values
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
        0 -> TypeSpec.objectBuilder(name).addModifiers(KModifier.DATA)
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
) : AggregateCoder<FileSpec, FunSpec, PropertySpec, TypeSpec>({}, root, main, test) {
    companion object {
        private val aggregateType = Aggregate::class.asTypeName()
        private val aggregateApplicationServiceType = AggregateApplicationService::class.asTypeName()
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

    private fun application(suffix: String) = ClassName(application, "$rootName$suffix")

    override fun codeAggregate() = TypeSpec.classBuilder(rootName + "Aggregate")
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
            .returns("$rootName${construct("id")}.also { it.emit = this }"))
        .addFunctions(eventCoders.map { it.codeAggregate() })
        .build()

    override fun codeApplication(types: List<TypeSpec>) = FileSpec
        .builder(application, rootName + "Application").addAnnotation(SUPPRESS_UNUSED).addTypes(types).build()

    override fun codeApplicationTest(property: PropertySpec, types: List<TypeSpec>) =
        FileSpec.builder(application, rootName + "ApplicationTest").addProperty(property).addTypes(types).build()

    override fun codeCommands() = TypeSpec.interfaceBuilder(rootName + "Commands")
        .addFunctions(commandCoders.map { it.codeCommands() })
        .build()

    override fun codeDomain(types: List<TypeSpec>) =
        FileSpec.builder(domain, rootName + "Events").addTypes(types).build()

    override fun codeDomainTest(type: TypeSpec) = FileSpec.get(domain, type)

    override fun codeEvent() = TypeSpec.sealedInterfaceBuilder(eventType).addSuperinterface(rootEventType).build()

    override fun codeSerializer() = PropertySpec
        .privateBuilder("serializer", serializerType.parameterizedBy(rootType))
        .initializer("%T.serializer()", rootType)
        .build()

    override fun codeService() = TypeSpec.objectBuilder("${rootName}Service")
        .superclass(aggregateApplicationServiceType.parameterizedBy(rootType, idType, eventType))
        .addSuperclassConstructorParameter("%T::class", rootType)
        .addFunctions(commandCoders.map { it.codeService() })
        .build()

    override fun codeSpec() = TypeSpec.abstractClassBuilder(specType)
        .addAnnotation(SUPPRESS_UNUSED_PRIVATE)
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
            addProperty(PropertySpec.initialized(testIdName, idType))
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

    override fun codeTestAggregate() = TypeSpec.classBuilder(rootName + "TestAggregate")
        .primaryConstructor("root", rootType)
        .superclass(genericTestAggregateType.parameterizedBy(rootType, eventsType, eventType))
        .addSuperclassConstructorParameter("root, serializer")
        .addSuperinterfaces(commandsType, eventsType)
        .addFunctions(commandCoders.map { it.codeTestAggregate() })
        .addFunctions(eventCoders.map { it.codeTestAggregate() })
        .build()

    private fun domain(suffix: String = "") = ClassName(domain, "$rootName$suffix")

    override fun AggregateCommand.toCoder() = KotlinPoetAggregateCommandCoder(this, toType)

    override fun AggregateEvent.toCoder() = KotlinPoetAggregateEventCoder(this, eventType)

    override fun FileSpec.toCodeFile() = KotlinPoetFile(this)
}

class KotlinPoetBoundedContextCoder(
    boundedContext: BoundedContext,
    main: GeneratedSources = GeneratedSources(),
    test: GeneratedSources = GeneratedSources(true)
) : BoundedContextCoder<FileSpec, TypeSpec>({}, boundedContext, main, test) {
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
