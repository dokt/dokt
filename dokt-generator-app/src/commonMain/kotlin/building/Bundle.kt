@file:Suppress("unchecked_cast")

package app.dokt.generator.building

abstract class Bundle<B : Bundle<B>>(
    val directory: String,

    val hasSources: Boolean,

    val hasTests: Boolean,

    val layer: Layer,

    val name: String,

    val parent: B?,

    val platform: Platform
) {
    val all: List<B> by lazy { listOf(this as B) + children.flatMap { it.all } }

    abstract val children: List<B>

    val descendants by lazy { children.flatMap { it.all } }

    abstract val exports: Set<String>

    abstract val imports: Set<String>

    val isDomain get() = layer == Layer.DOMAIN

    val isInterface get() = layer == Layer.INTERFACE

    val isJs get() = platform == Platform.JS

    val isJvm get() = platform == Platform.JVM

    val isMulti get() = platform == Platform.MULTI

    val isRoot get() = parent == null

    abstract val mainClass: String?

    /** Bundle path which starts with colon. */
    val path: String = if (parent == null) ":" else if (parent.isRoot) ":$name" else "${parent.path}:$name"

    val root: B by lazy { parent?.root ?: this as B }

    operator fun get(path: String) = children.find { it.name == path } ?: root.all.find { it.path == path }
    ?: throw Exception("Project not found by path '$path'!")

    fun printHierarchy(indent: Int = 0) {
        repeat(indent) { print('\t') }
        println(this)
        children.forEach { it.printHierarchy(indent + 1) }
    }

    abstract fun imports(packagePrefix: String): Boolean

    override fun toString() = "$name, $layer to $platform"
}