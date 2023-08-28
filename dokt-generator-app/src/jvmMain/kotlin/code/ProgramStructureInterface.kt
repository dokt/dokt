/** [Program Structure Interface (PSI)](https://plugins.jetbrains.com/docs/intellij/psi.html) */
package app.dokt.generator.code

import app.dokt.generator.code.psi.Psi
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiNamedElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.File
import java.io.PrintStream
import java.nio.file.Path

class KotlinClass(private val file: KotlinFile, element: KtClassOrObject)
    : PackagedElement<KtClassOrObject>(element), TypeDef {

    override val implements by lazy {
        element.superTypeListEntries.map {
            KotlinType(file, requireNotNull(it.typeReference) { "Super type must have type reference!" })
        }
    }

    override val isData get() = element is KtClass && element.isData()

    override val isEnumeration get() = element is KtClass && element.isEnum()

    override val isInterface get() = element is KtClass && element.isInterface()

    override val isValue get() = element is KtClass && element.isValue()

    override val methods by lazy { element.body?.functions?.map { KotlinFunction(it, file) }.orEmpty() }

    override val packageName get() = file.packageName

    override val primaryConstructor by lazy { element.primaryConstructorParameters.map { KotlinVariable(file, it) } }

    override val properties by lazy {
        if (isData || isValue) primaryConstructor
        else TODO()
    }
}

private const val PACKAGE_PREFIX = "package "

/**
 * [Parsing Kotlin code using Kotlin](https://jitinsharma.in/posts/parsing-kotlin-using-code-kotlin/)
 */
class KotlinFile(val file: File, element: KtFile) : PackagedElement<KtFile>(element), CodeFile {
    val hasMain get() = element.getChildrenOfType<KtNamedFunction>().any { it.name == "main" }

    /**
     * Qualified import names by alias name
     *
     * TODO Handle alias and multiple simple names
     */
    val imports by lazy {
        element.importDirectives.associate { import -> import.text.removePrefix("import ").let {
            it.substringAfterLast('.') to it.substringBeforeLast('.')
        } }
    }

    val javaClassName get() = "$packageName.$javaFileName"

    val javaFileName get() = name.substringBefore('.') + "Kt"

    private val log by lazy { KotlinLogging.logger {  } }

    override val path: String get() = file.path

    override val packageName by lazy {
        (element.packageDirective?.qualifiedName.orEmpty()).ifEmpty {
            // TODO fix PSI unable to parse package!
            log.warn { "PSI unable to parse package! Reading file '$file' manually!" }
            file.readLines().firstOrNull { it.startsWith(PACKAGE_PREFIX) }?.substring(PACKAGE_PREFIX.length).orEmpty()
        }
    }

    override val types by lazy { element.getChildrenOfType<KtClassOrObject>().map { KotlinClass(this, it) } }

    constructor(file: File) : this(file, Psi.read(file))

    constructor(path: Path) : this(path.toFile())

    /**
     * Try to find package name.
     *
     * TODO handle [default imports](https://kotlinlang.org/docs/packages.html#default-imports)
     */
    fun getPackageNameFor(name: String) = imports[name] ?: imports["*"] ?: "kotlin.$name"

    fun imports(packagePrefix: String) = imports.values.any { it.startsWith(packagePrefix) }
}

class KotlinFunction(element: KtNamedFunction, private val file: KotlinFile)
    : PackagedElement<KtNamedFunction>(element), Method {
    override val packageName get() = file.packageName

    override val parameters by lazy { element.valueParameters.map { KotlinVariable(file, it) } }
}

class KotlinType(private val file: KotlinFile, private val type: KtTypeElement) : TypeRef {

    constructor(file: KotlinFile, reference: KtTypeReference) : this(file, reference.typeElement as KtTypeElement)

    /**
     * Generic type arguments
     */
    override val arguments by lazy { type.typeArgumentsAsTypes.map { KotlinType(file, it) } }

    private val label by lazy { "$name${if (arguments.isEmpty()) "" else "<${arguments.joinToString()}>"}" }

    /**
     * If KtTypeReference is fully qualified and not imported it doesn't work.
     */
    override val name get() = when (type) {
        is KtUserType -> requireNotNull(type.referencedName) { "User type should have referenced name!" }
        is KtNullableType -> requireNotNull(type.text).removeSuffix("?")
        else -> TODO()
    }

    override val nullable get() = type is KtNullableType

    override val packageName by lazy {
        if (file.types.any { it.name == name }) file.packageName else file.getPackageNameFor(name)
    }

    override val simpleNames = name.split('.')

    override fun toString() = label
}

class KotlinVariable(private val file: KotlinFile, element: KtNamedDeclaration, typeReference: KtTypeReference)
    : PackagedElement<KtNamedDeclaration>(element), Variable {

    constructor(file: KotlinFile, parameter: KtParameter) :
        this(file, parameter, requireNotNull(parameter.typeReference) { "Parameter must have type reference!" })

    constructor(file: KotlinFile, property: KtProperty) :
        this(file, property, requireNotNull(property.typeReference) { "Property must have type reference!" })

    override val packageName get() = file.packageName

    override val type = KotlinType(file, typeReference)

    override fun toString() = "$name: $type"
}

open class PackagedElement<E : PsiNamedElement>(val element: E) : Packaged {
    override val name get() = element.name.orEmpty()

    override fun toString() = qualifiedName
}

fun PsiElement.println(stream: PrintStream = System.out, indent: String = "") {
    stream.println("$indent${this::class.simpleName}: $this '$text'")
    children.forEach { it.println(stream, "$indent  ") }
}
