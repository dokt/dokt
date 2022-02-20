/**
 * [Program Structure Interface (PSI)](https://plugins.jetbrains.com/docs/intellij/psi.html)
 */
package app.dokt.generator.code

import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.io.*
import java.nio.file.Path

class KotlinClass(private val file: KotlinFile, element: KtClass) : PackagedElement<KtClass>(element), TypeDef {

    override val implements by lazy {
        element.superTypeListEntries.map { KotlinType(file, it.typeReference!!) }
    }

    override val isData get() = element.isData()

    override val isEnumeration get() = element.isEnum()

    override val isInterface get() = element.isInterface()

    override val methods by lazy { element.body?.functions?.map { KotlinFunction(it, file) } ?: emptyList() }

    override val packageName get() = file.packageName

    override val primaryConstructor by lazy { element.primaryConstructorParameters.map { KotlinVariable(file, it) } }

    override val properties by lazy {
        if (element.isData()) primaryConstructor
        else TODO()
    }
}

/**
 * [Parsing Kotlin code using Kotlin](https://jitinsharma.in/posts/parsing-kotlin-using-code-kotlin/)
 */
class KotlinFile(val file: File, element: KtFile) : PackagedElement<KtFile>(element), CodeFile {
    companion object {
        private val manager = PsiManager.getInstance(KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.METADATA_CONFIG_FILES
        ).project)

        private fun read(file: File) =
            manager.findFile(LightVirtualFile(file.name, KotlinFileType.INSTANCE, file.readText())) as KtFile
    }

    constructor(file: File) : this(file, read(file))

    constructor(path: Path) : this(path.toFile())

    /**
     * Qualified import names by alias name
     *
     * TODO Handle alias and multiple simple names
     */
    val imports by lazy {
        element.importDirectives.associate { import -> import.importedFqName!!.asString().let {
            it.substringAfterLast('.') to it.substringBeforeLast('.')
        } }
    }

    private val log by lazy { mu.KotlinLogging.logger {  } }

    override val path: String get() = file.path

    override val packageName by lazy {
        (element.packageDirective?.qualifiedName ?: "").ifEmpty {
            log.warn { "PSI unable to parse package! Reading file '$file' manually!" }
            file.readLines().firstOrNull { it.startsWith("package ") }?.substring(8) ?: ""
        }
    }

    override val types by lazy { element.getChildrenOfType<KtClass>().map { KotlinClass(this, it) } }

    /**
     * Try to find package name.
     *
     * TODO handle [default imports](https://kotlinlang.org/docs/packages.html#default-imports)
     */
    fun getPackageNameFor(name: String) = imports.getOrDefault(name, "kotlin.$name")
}

class KotlinFunction(element: KtNamedFunction, private val file: KotlinFile)
    : PackagedElement<KtNamedFunction>(element), Method {
    override val packageName get() = file.packageName

    override val parameters by lazy { element.valueParameters.map { KotlinVariable(file, it) } }
}

class KotlinType(private val file: KotlinFile, private val type: KtUserType) : TypeRef {

    constructor(file: KotlinFile, reference: KtTypeReference) : this(file, reference.typeElement as KtUserType)

    /**
     * Generic type arguments
     */
    override val arguments by lazy { type.typeArgumentsAsTypes.map { KotlinType(file, it) } }

    private val label by lazy { "$name${if (arguments.isEmpty()) "" else "<${arguments.joinToString()}>"}" }

    /**
     * If KtTypeReference is fully qualified and not imported it doesn't work.
     */
    override val name get() = type.referencedName!!

    override val packageName by lazy { file.getPackageNameFor(name) }

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

abstract class PackagedElement<E : PsiNamedElement>(protected val element: E) : Packaged {
    override val name get() = element.name ?: ""

    override fun toString() = qualifiedName
}

fun PsiElement.println(stream: PrintStream = System.out, indent: String = "") {
    stream.println("$indent${this::class.simpleName}: $this '$text'")
    children.forEach { it.println(stream, "$indent  ") }
}
