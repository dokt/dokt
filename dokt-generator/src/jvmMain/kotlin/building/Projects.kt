package app.dokt.generator.building

import app.dokt.generator.building.Layer.*
import app.dokt.*
import java.nio.file.Path
import kotlin.io.path.*

sealed class GradleProject(
    parent: GradleProject?,
    val dir: Path,
    name: String,
    layer: Layer,
    platform: Platform,
    src: SourceDir
) : Bundle<GradleProject>(dir.toString(), src.hasSources, src.hasTests, layer, name, parent, platform) {
    override val children by lazy {
        dir.filterDirectoryEntries { path ->
            path.isDirectory() && !path.name.let {
                it.startsWith(".") || ignoredDirectories.contains(it) || it.startsWith("dokt-") }
        }.map { parse(it, this) }
    }

    companion object {
        val applications = mutableListOf<MultiProject>()
        val domains = mutableListOf<MultiProject>()
        private val ignoredDirectories = setOf("build", "doc", "gradle", "src")
        val infrastructures = mutableListOf<GradleProject>()
        val interfaces = mutableListOf<SingleProject>()

        fun parse(dir: Path, parent: GradleProject? = null, name: String = dir.name): GradleProject {
            val names = dir.flatMap { it.name.split('-') }
            val layer = Layer.parse(names)
            val platform = Platform.parse(layer, names)
            return if (platform.isMulti) MultiProject(parent, dir, name, layer, platform).apply {
                if (hasSources) when (layer) {
                    APPLICATION -> applications.add(this)
                    DOMAIN -> domains.add(this)
                    INFRASTRUCTURE -> infrastructures.add(this)
                    else -> throwIllegalState()
                }
            } else SingleProject(parent, dir, name, layer, platform).apply {
                if (hasSources) when (layer) {
                    INFRASTRUCTURE -> infrastructures.add(this)
                    INTERFACE -> interfaces.add(this)
                    else -> throwIllegalState()
                }
            }
        }
    }
}

class MultiProject(
    parent: GradleProject?,
    dir: Path,
    name: String,
    layer: Layer,
    platform: Platform,
    val src: MultiSourceDir = MultiSourceDir(dir)
) : GradleProject(parent, dir, name, layer, platform, src) {
    override val exports by lazy { (src.common.main.exports + src.jvm.main.exports).toSortedSet() }

    override val imports by lazy { (src.common.main.imports + src.jvm.main.imports).toSortedSet() }

    override val mainClass by lazy { src.common.main.mainClass ?: src.jvm.main.mainClass }

    override fun imports(packagePrefix: String) = src.imports(packagePrefix)
}

class SingleProject(
    parent: GradleProject?,
    dir: Path,
    name: String,
    layer: Layer,
    platform: Platform,
    val src: SingleSourceDir = SingleSourceDir(dir)
) : GradleProject(parent, dir, name, layer, platform, src) {
    override val exports by lazy { src.main.exports }

    override val imports by lazy { src.main.imports }

    override val mainClass by lazy { src.main.mainClass }

    override fun imports(packagePrefix: String) = src.imports(packagePrefix)
}