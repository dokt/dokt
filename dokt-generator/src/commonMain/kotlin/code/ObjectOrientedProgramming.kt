/**
 * Object-oriented programming (OOP)
 */
package app.dokt.generator.code

import app.dokt.generator.commonPrefix
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * Source code file
 */
interface CodeFile : Packaged {
    val types: List<TypeDef>

    val path: String
}

/**
 * Class method or function like a procedure. Function as a name conflicts with [kotlin.Function].
 */
interface Method : Packaged {
    /**
     * Parameters
     */
    val parameters: List<Variable>
}

interface Packaged {
    /**
     * Simple names e.g. Map.Entry or FileName without extension
     * Anonymous type has empty name.
     */
    val name: String

    /**
     * Package qualified name e.g. app.oop.model
     * Default package is empty string.
     */
    val packageName: String get() = ""

    val qualifiedName get() = if (packageName.isBlank()) name else "$packageName.$name"
}

/**
 * Type definition
 */
interface TypeDef : Packaged {
    val extends get() = implements.firstOrNull()

    /**
     * All types which are implemented by this class. Super type is first if extended.
     */
    val implements: List<TypeRef>

    /**
     * Is data type
     */
    val isData: Boolean

    val isEnumeration: Boolean

    val isException get() = extends?.name?.endsWith("Exception") ?: false

    val isInterface: Boolean

    val methods: List<Method>

    /**
     * Primary constructor parameters
     */
    val primaryConstructor : List<Variable>

    val properties: List<Variable>

    val reference get() = Ref(qualifiedName)
}

/**
 * Type reference
 */
interface TypeRef : Packaged {
    /**
     * Generic type arguments
     */
    val argument get() = arguments.first()

    /**
     * Generic type arguments
     */
    val arguments: List<TypeRef>

    val isCollection get() = name.endsWith("Collection") || isList

    val isList get() = name.endsWith("List")

    /**
     * Simple names eg. Map.Entry
     */
    val simpleNames: List<String>
}

@JvmInline
@Serializable
value class Ref(override val qualifiedName: String) : TypeRef {
    override val arguments get() = qualifiedName
        .substringAfter('<')
        .substringBeforeLast('>')
        .split(',')
        .map { Ref(it.trim()) }

    override val simpleNames get() = qualifiedName
        .substringBefore('<')
        .split('.')
        .takeLastWhile { it[0].isUpperCase() }

    override val name get() = simpleNames.joinToString(".")

    override val packageName get() = qualifiedName
        .substringBefore('<')
        .split('.')
        .takeWhile { it[0].isLowerCase() }
        .joinToString(".")

    override fun toString() = qualifiedName

    constructor(kClass: KClass<*>) : this(kClass.qualifiedName!!)

    companion object {
        val unit = Ref(Unit::class)
    }
}

/**
 * Parameter, property or any variable
 * TODO is read-only
 */
interface Variable : Packaged {
    val type: TypeRef

    fun isString() = type.name == "String" // Package may be java.lang or kotlin.
}

@Serializable
data class Var(
    override val name: String,

    override val type: TypeRef,

    /**
     * empty package name means it's a class variable or in default package.
     */
    override val packageName: String = ""
) : Variable

val Iterable<Packaged>.commonPackage get() = map { it.packageName }.commonPackage
val Iterable<Packaged>.names get() = joinToString { it.name }
val Collection<String>.commonPackage get() = commonPrefix.removeSuffix(".")

val stringRef = Ref(String::class)
val String.kotlinRef get() = Ref("kotlin.$this")
val String.ref get() = Ref(this)
