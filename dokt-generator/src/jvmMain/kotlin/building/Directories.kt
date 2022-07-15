package app.dokt.generator.building

import app.dokt.generator.code.KotlinSources
import java.nio.file.Path
import kotlin.io.path.exists

open class Dir(parent: Path, name: String) {
    val path: Path = parent.resolve(name)
    val dir by lazy { if (exists) path else null }
    val exists by lazy { path.exists() }
}

/** Multiplatform source directory */
class MultiSourceDir(parent: Path) : SourceDir(parent) {
    override val hasSources by lazy { exists && (common.hasSources || jvm.hasSources || js.hasSources) }
    override val hasTests by lazy { exists && (common.hasTests || jvm.hasTests || js.hasTests) }
    val common by lazy { SingleSourceDir(parent, "common") }
    val js by lazy { SingleSourceDir(parent, Platform.JS.id) }
    val jvm by lazy { SingleSourceDir(parent, Platform.JVM.id) }

    override fun imports(packagePrefix: String) =
        common.imports(packagePrefix) || js.imports(packagePrefix) || jvm.imports(packagePrefix)
}

/** Single platform source directory */
class SingleSourceDir(parent: Path, platformId: String? = null) : SourceDir(parent) {
    override val hasSources by lazy { exists && main.hasKotlin }
    override val hasTests by lazy { exists && test.hasKotlin }
    val main by lazy { SourceSetDir(this, platformId?.let { "${it}Main"} ?: "main") }
    val test by lazy { SourceSetDir(this, platformId?.let { "${it}Test"} ?: "test") }

    override fun imports(packagePrefix: String) = (hasSources && main.imports(packagePrefix))
            || (hasTests && test.imports(packagePrefix))
}

abstract class SourceDir(parent: Path) : Dir(parent, "src") {
    abstract val hasSources: Boolean
    abstract val hasTests: Boolean

    abstract fun imports(packagePrefix: String): Boolean
}

class SourceSetDir(parent: SingleSourceDir, name: String) : Dir(parent.path, name) {
    val exports by lazy { kotlin.exportPackages }
    val hasKotlin by lazy { exists && kotlin.exists }
    val imports by lazy { kotlin.importTypes }
    val mainClass get() = kotlin.mainFile?.javaClassName
    val kotlin by lazy { KotlinSources(path) }

    fun imports(packagePrefix: String) = hasKotlin && kotlin.imports(packagePrefix)
}
