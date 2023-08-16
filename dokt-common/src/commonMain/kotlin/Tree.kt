package app.dokt.common

import kotlin.math.max

/**
 * A [tree structure](https://en.wikipedia.org/wiki/Tree_structure) element.
 *
 * [Data structure](https://en.wikipedia.org/wiki/Tree_(data_structure))
 */
interface Node : Iterable<Node> {
    /** A nodes reachable by repeated proceeding from child to parent. */
    val ancestors: List<Node> get() = parent?.let { listOf(it) + it.ancestors } ?: emptyList()

    /** The number of leaves. */
    val breadth get() = children.count { it.leaf }

    /** Zero or more child nodes, which are below it in the tree. */
    val children: List<Node>

    /** For a given node, its number of children. A leaf has necessarily degree zero. */
    val degree get() = children.size

    /** The degree of a tree is the maximum degree of a node in the tree. */
    val degreeOfTree get() = max(degree, children.maxOf { it.degree })

    /** A node reachable by repeated proceeding from parent to child. */
    val descendants: List<Node> get() = children.flatMap { listOf(it) + it.descendants }

    /** The height of a node is the length of the longest downward path to a leaf from that node. */
    val height: Int get() = if (leaf) 0 else 1 + children.maxOf { it.height }

    /** Leaf nodes doesn't have children. */
    val leaf get() = children.isEmpty()

    /** The level of a node is the number of edges along the unique path between it and the root node. */
    val level: Int get() = parent?.let { 1 + it.level } ?: 0

    /** A node that has a child is called the child's parent node. */
    val parent: Node?

    /** The topmost root node */
    val root: Node get() = parent?.root ?: this

    /** Child nodes with the same parent */
    val siblings get() = parent?.let { it.children - this } ?: emptyList()

    /** Number of nodes in the (sub) tree. */
    val size: Int get() = 1 + children.sumOf { it.size }

    /** The number of nodes in a level. */
    val width get() = parent?.degree ?: 1

    /** Iterator of this branch */
    override fun iterator() = (listOf(this) + descendants).iterator()

    fun toString(builder: StringBuilder, indent: Int) {
        repeat(indent) { builder.append("  ") }
        builder.append("- $this\n")
        children.forEach { it.toString(builder, indent + 1) }
    }
}

interface MutableNode : Node {
    override val children: MutableList<Node>

    override var parent: Node?

    fun add(child: MutableNode) {
        children.add(child)
        child.parent = this
    }

    fun remove(child: MutableNode) {
        if (child.parent != this) throw IllegalArgumentException("$child isn't child of $this!")
        child.parent = null
        children.remove(child)
    }
}

open class BaseNode(
    override val children: MutableList<Node> = mutableListOf(),
    override var parent: Node? = null
) : MutableNode {
    constructor(vararg children: MutableNode) : this() { children.forEach(::add) }
}