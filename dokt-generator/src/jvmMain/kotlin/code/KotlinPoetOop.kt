/**
 * [KotlinPoet](https://square.github.io/kotlinpoet/) extensions for OOP
 */
package app.dokt.generator.code

import app.dokt.generator.*
import com.squareup.kotlinpoet.*
import java.io.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writer

fun codeBlock(code: CodeBlock.Builder.() -> Unit) = CodeBlock.builder().apply { code() }.build()

fun controlFlow(controlFlow: String, vararg args: Any?, code: CodeBlock.Builder.() -> Unit) = codeBlock {
    controlFlow(controlFlow, *args) { code() }
}

fun script(fileName: String, code: FileSpec.Builder.() -> Unit) = FileSpec
    .scriptBuilder(fileName).apply { code() }.build()

val Array<out ClassName>.asParameters get() = map { ParameterSpec(it.simpleName.lowerFirst(), it) }

val Array<out Pair<String, TypeName>>.asParameters get() = map { ParameterSpec(it.first, it.second) }

fun CodeBlock.Builder.controlFlow(controlFlow: String, vararg args: Any?, code: CodeBlock.Builder.() -> Unit):
        CodeBlock.Builder {
    beginControlFlow(controlFlow, *args)
    code()
    return endControlFlow()
}

fun FileSpec.print() {
    println()
    println("Printing $name file:")
    println("-----")
    writeTo(System.out)
}

fun FileSpec.Builder.addTypes(types: Array<out TypeSpec>): FileSpec.Builder {
    types.forEach { addType(it) }
    return this
}

fun FileSpec.Builder.addTypes(types: Iterable<TypeSpec>): FileSpec.Builder {
    types.forEach { addType(it) }
    return this
}

fun FileSpec.Builder.print() = build().print()

fun FileSpec.write(dir: Path, commonRoot: String = "") {
    var path = dir
    // Resolve rest package component directories.
    packageName.removePrefix(commonRoot).split('.').forEach {
        if (it.isNotBlank()) path = path.resolve(it)
    }
    path.createDirectories()
    KotlinPoetSanitizer(path.resolve("$name.kt").writer()).use { writeTo(it) }
}

fun FileSpec.write(sources: Sources) = write(Path(sources.basePath), sources.commonRootPackage)

fun FunSpec.Builder.beginReturn(controlFlow: String, vararg args: Any) =
    beginControlFlow("return $controlFlow", *args)

fun FunSpec.Builder.returns(format: String, vararg args: Any) = addCode("return $format", *args).build()

fun FunSpec.Companion.abstract(name: String, parameters: Iterable<ParameterSpec>) =
    abstractBuilder(name, parameters).build()

fun FunSpec.Companion.abstractBuilder(name: String) = builder(name).addModifiers(KModifier.ABSTRACT)

fun FunSpec.Companion.abstractBuilder(name: String, parameters: Iterable<ParameterSpec>) =
    abstractBuilder(name).addParameters(parameters)

fun FunSpec.Companion.abstract(
    name: String,
    parameters: List<Variable> = emptyList(),
    returnType: TypeRef = Ref.unit
) = builder(name)
    .addModifiers(KModifier.ABSTRACT)
    .addParameters(parameters.asParameters)
    .returns(returnType.asClassName)
    .build()

fun FunSpec.Companion.constructor(vararg types: ClassName) =
    constructorBuilder().addParameters(types.asParameters).build()

fun FunSpec.Companion.constructor(name: String, type: TypeName, vararg modifiers: KModifier) =
    constructorBuilder(name, type, *modifiers).build()

fun FunSpec.Companion.constructorBuilder(name: String, type: TypeName, vararg modifiers: KModifier) =
    constructorBuilder().addParameter(name, type, *modifiers)

fun FunSpec.Companion.overrideBuilder(name: String) = builder(name).addModifiers(KModifier.OVERRIDE)

fun FunSpec.Companion.overrideBuilder(name: String, parameters: Iterable<ParameterSpec>) =
    overrideBuilder(name).addParameters(parameters)

fun FunSpec.Companion.overrideBuilder(name: String, parameters: List<Variable>) =
    overrideBuilder(name, parameters.asParameters)

fun FunSpec.Companion.overrideBuilder(name: String, vararg parameters: Pair<String, TypeName>) =
    overrideBuilder(name, parameters.asParameters)

fun FunSpec.Companion.overrideBuilder(name: String, returns: TypeName, vararg parameters: Pair<String, TypeName>) =
    overrideBuilder(name, parameters.asParameters).returns(returns)

fun FunSpec.Companion.suspendingBuilder(name: String) = builder(name).addModifiers(KModifier.SUSPEND)

class KotlinPoetFile(val spec: FileSpec) : CodeFile {
    override val types = emptyList<TypeDef>()
    override var path = ""
    override val name = spec.name
    override val packageName = spec.packageName
}

/**
 * Removes following from KotlinPoet output:
 * - Unnecessary Kotlin core imports
 * - Redundant public modifiers
 * - Redundant Unit return types
 */
class KotlinPoetSanitizer(private val out: Writer) : Appendable, Closeable {
    private val builder = StringBuilder()

    override fun append(csq: CharSequence): Appendable {
        csq.forEach { append(it) }
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int) = append(csq.subSequence(start, end))

    override fun append(c: Char): Appendable {
        builder.append(c)
        if (c == '\n') {
            var line = builder.toString()
            // Ignore kotlin.* but allow e.g. kotlin.jvm.Inline
            if (!(line.startsWith("import kotlin.") && line[14].isUpperCase())) {
                line = line
                    .replace("public ", "")
                    .replace(": Unit", "")
                out.write(line)
            }
            builder.clear()
        }
        return this
    }

    override fun close() = out.close()
}

fun LambdaTypeName.Companion.get(
    receiver: TypeName? = null,
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName = UNIT,
    nullable: Boolean
) = get(receiver, parameters, returnType).copy(nullable)

fun LambdaTypeName.Companion.get(
    suspending: Boolean = false,
    receiver: TypeName? = null,
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName = UNIT,
    nullable: Boolean = false
) = get(receiver, parameters, returnType).copy(nullable, suspending = suspending)

fun LambdaTypeName.Companion.nullable(
    receiver: TypeName? = null,
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName = UNIT) = get(false, receiver, parameters, returnType, true)

fun LambdaTypeName.Companion.of(
    receiver: TypeName? = null,
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName = UNIT
) = get(receiver, parameters, returnType)

fun LambdaTypeName.Companion.suspending(
    receiver: TypeName? = null,
    parameters: List<ParameterSpec> = emptyList(),
    returnType: TypeName = UNIT) = get(true, receiver, parameters, returnType)


val List<Variable>.asConstructor get() = FunSpec.constructorBuilder().let { builder ->
    forEach { builder.addParameter(it.name, it.asClassName) }
    builder.build()
}

/**
 * Variables as constructor initialized properties.
 */
val List<Variable>.asInitialized get() = map { it.asInitializedProperty }

val List<Variable>.asParameters get() = map { ParameterSpec(it.name, it.asClassName) }

fun Method.overrideBuilder() = FunSpec.overrideBuilder(name, parameters)

fun PropertySpec.Companion.initialized(name: String, type: TypeName) =
    builder(name, type).initializer(name).build()

fun PropertySpec.Companion.privateBuilder(name: String, type: TypeName) =
    builder(name, type, KModifier.PRIVATE)

fun PropertySpec.Companion.privateInitialized(name: String, type: TypeName) =
    privateBuilder(name, type).initializer(name).build()

fun PropertySpec.Companion.protectedBuilder(name: String, type: TypeName) =
    builder(name, type, KModifier.PROTECTED)

fun PropertySpec.Companion.protectedInitialized(name: String, type: TypeName) =
    protectedBuilder(name, type).initializer(name).build()

val TypeName.asNullable get() = copy(true)

val TypeRef.asClassName get() = ClassName(packageName, simpleNames).let {
    if (nullable) it.asNullable else it
}

fun TypeSpec.Builder.addSuperinterfaces(vararg superinterfaces: TypeName) =
    addSuperinterfaces(superinterfaces.asIterable())

fun TypeSpec.Builder.primaryConstructor(name: String, type: TypeName, vararg modifiers: KModifier) =
    primaryConstructor(FunSpec.constructor(name, type, *modifiers))

fun TypeSpec.Companion.abstractClassBuilder(name: String) = classBuilder(name).addModifiers(KModifier.ABSTRACT)

fun TypeSpec.Companion.abstractClassBuilder(type: ClassName) = abstractClassBuilder(type.simpleName)

fun TypeSpec.Companion.classBuilder(type: ClassName, vararg params: ClassName) =
    classBuilder(type).primaryConstructor(FunSpec.constructor(*params))

/**
 * Get class builder with constructor.
 */
fun TypeSpec.Companion.classBuilder(name: String, vararg params: ClassName) =
    classBuilder(name).primaryConstructor(FunSpec.constructor(*params))

/**
 * Get data class builder with name and properties.
 */
fun TypeSpec.Companion.dataClassBuilder(name: String, properties: List<Variable>) =
    classBuilder(name).addModifiers(KModifier.DATA).primaryConstructorProperties(properties)

fun TypeSpec.Companion.sealedInterfaceBuilder(className: ClassName) = TypeSpec.interfaceBuilder(className)
    .addModifiers(KModifier.SEALED)

/**
 * Build object.
 */
fun TypeSpec.Companion.singleton(name: String) = objectBuilder(name).build()

/**
 * Get value class builder with name and property.
 */
fun TypeSpec.Companion.valueClassBuilder(name: String, property: Variable) =
    valueClassBuilder(name).addAnnotation(JvmInline::class).primaryConstructorProperties(listOf(property))

/**
 * Build value class with name and properties.
 */
fun TypeSpec.Companion.valueClass(name: String, property: Variable) =
    valueClassBuilder(name, property).build()

fun TypeSpec.Builder.primaryConstructorProperties(properties: List<Variable>) =
    primaryConstructor(properties.asConstructor).addProperties(properties.asInitialized)

val Variable.asConstructor get() = FunSpec.constructorBuilder().addParameter(name, asClassName).build()

val Variable.asClassName get() = type.asClassName

/**
 * Variable as constructor initialized property.
 */
val Variable.asInitializedProperty get() = PropertySpec.builder(name, asClassName).initializer(name).build()

val Variable.asProperty get() = PropertySpec.builder(name, asClassName).build()
