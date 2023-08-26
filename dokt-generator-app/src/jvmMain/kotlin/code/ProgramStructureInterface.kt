/** [Program Structure Interface (PSI)](https://plugins.jetbrains.com/docs/intellij/psi.html) */
package app.dokt.generator.code

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.*
import java.nio.file.Path

class KotlinClass(private val file: KotlinFile, element: KtClassOrObject)
    : PackagedElement<KtClassOrObject>(element), TypeDef {

    override val implements by lazy {
        element.superTypeListEntries.map { KotlinType(file, it.typeReference!!) }
    }

    override val isData get() = element is KtClass && element.isData()

    override val isEnumeration get() = element is KtClass && element.isEnum()

    override val isInterface get() = element is KtClass && element.isInterface()

    override val isValue get() = element is KtClass && element.isValue()

    override val methods by lazy { element.body?.functions?.map { KotlinFunction(it, file) } ?: emptyList() }

    override val packageName get() = file.packageName

    override val primaryConstructor by lazy { element.primaryConstructorParameters.map { KotlinVariable(file, it) } }

    override val properties by lazy {
        if (isData || isValue) primaryConstructor
        else TODO()
    }
}

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
        (element.packageDirective?.qualifiedName ?: "").ifEmpty {
            log.warn { "PSI unable to parse package! Reading file '$file' manually!" }
            file.readLines().firstOrNull { it.startsWith("package ") }?.substring(8) ?: ""
        }
    }

    override val types by lazy { element.getChildrenOfType<KtClassOrObject>().map { KotlinClass(this, it) } }

    constructor(file: File) : this(file, read(file))

    constructor(path: Path) : this(path.toFile())

    /**
     * Try to find package name.
     *
     * TODO handle [default imports](https://kotlinlang.org/docs/packages.html#default-imports)
     */
    fun getPackageNameFor(name: String) = imports[name] ?: imports["*"] ?: "kotlin.$name"

    fun imports(packagePrefix: String) = imports.values.any { it.startsWith(packagePrefix) }

    companion object {
        private val manager = PsiManager.getInstance(KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.METADATA_CONFIG_FILES
        ).project)

        private fun read(file: File) =
            manager.findFile(LightVirtualFile(file.name, KotlinFileType.INSTANCE, file.readText())) as KtFile
    }
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
        is KtUserType -> type.referencedName!!
        is KtNullableType -> type.text!!.removeSuffix("?")
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

    constructor(file: KotlinFile, parameter: KtParameter) : this(file, parameter, parameter.typeReference!!)

    constructor(file: KotlinFile, property: KtProperty) : this(file, property, property.typeReference!!)

    override val packageName get() = file.packageName

    override val type = KotlinType(file, typeReference)

    override fun toString() = "$name: $type"
}

abstract class PackagedElement<E : PsiNamedElement>(val element: E) : Packaged {
    override val name get() = element.name ?: ""

    override fun toString() = qualifiedName
}

fun PsiElement.println(stream: PrintStream = System.out, indent: String = "") {
    stream.println("$indent${this::class.simpleName}: $this '$text'")
    children.forEach { it.println(stream, "$indent  ") }
}
