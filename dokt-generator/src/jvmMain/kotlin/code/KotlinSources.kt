package app.dokt.generator.code

import app.dokt.common.pluralize
import app.dokt.generator.building.Dir
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path
import kotlin.io.path.*

class KotlinSources(path: Path) : Dir(path, "kotlin"), Sources {
    override val basePath = path.toString()
    val exportPackages by lazy { typesByPackages.keys.filter { it.isNotBlank() }.toSortedSet() }
    val exportTypes by lazy { types.map { it.qualifiedName }.toSortedSet() }
    val files by lazy { filesByPath.flatMap { it.value } }
    private val filesByPath = mutableMapOf<Path, List<KotlinFile>>()
    val importTypes by lazy { files.flatMap { it.imports.values }.filterNot { langImports.contains(it) }.toSortedSet() }
    private val log = KotlinLogging.logger { }
    val mainFile by lazy { files.firstOrNull { it.hasMain } }
    override val types by lazy { typesByPackages.flatMap { it.value } }
    private val typesByPackages = mutableMapOf<String, MutableList<KotlinClass>>()
    override val commonRootPackage by lazy { typesByPackages.keys.commonPackage }

    init {
        if (exists) path.scan()
    }

    constructor(path: String) : this(Path(path))

    fun imports(packagePrefix: String) = files.any { it.imports(packagePrefix) }

    private fun Path.scan(): List<KotlinFile> {
        log.trace { "Scanning $this..." }
        val files = mutableListOf<KotlinFile>()
        filesByPath[relativeTo(path)] = files
        forEachDirectoryEntry {
            if (it.isDirectory()) it.scan()
            else if (it.extension == "kt") {
                try {
                    log.trace { "Parsing $it..." }
                    val file = KotlinFile(it)
                    files.add(file)
                    val packageName = file.packageName
                    val fileTypes = file.types
                    log.debug { "Parsed ${"type".pluralize(fileTypes)} to '$packageName' package." }
                    typesByPackages.getOrPut(packageName) { mutableListOf() }.addAll(fileTypes)
                } catch (e: Exception) {
                    log.error(e) { "Unable parse $it!" }
                }
            } else {
                log.warn { "Ignored $it." }
            }
        }
        log.debug { "Found ${files.size} Kotlin files at $this." }
        return files
    }

    companion object {
        val langImports = setOf("java")
    }
}
