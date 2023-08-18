package app.dokt.generator.code

/**
 * Source code set
 */
interface Sources {
    val basePath: String

    /**
     * A common root package which subdirectories may be omitted in Kotlin.
     */
    val commonRootPackage get() = types.commonPackage

    val types get() = emptyList<TypeDef>()

    fun getPackageTypes(packageName: String) = types.filter { it.packageName.startsWith(packageName) }
}

class GeneratedSources(override val basePath: String) : Sources {
    constructor(test: Boolean = false) : this("build/common${if (test) "Test" else "Main"}")

    override val commonRootPackage by lazy { files.commonPackage }

    /**
     * Files to generate
     */
    val files = mutableListOf<CodeFile>()

    override val types get() = files.flatMap { it.types }
}
