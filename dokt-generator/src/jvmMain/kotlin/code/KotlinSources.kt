package app.dokt.generator.code

import app.dokt.generator.pluralize
import java.nio.file.Path
import kotlin.io.path.*

class KotlinSources(private val base: Path) : Sources {
    override val basePath = base.toString()
    val files by lazy { filesByPath.flatMap { it.value } }
    private val filesByPath = mutableMapOf<Path, List<KotlinFile>>()
    private val log = mu.KotlinLogging.logger { }
    override val types by lazy { typesByPackages.flatMap { it.value } }
    private val typesByPackages = mutableMapOf<String, MutableList<KotlinClass>>()
    override val commonRootPackage by lazy { typesByPackages.keys.commonPackage }

    init {
        base.scan()
    }

    private fun Path.scan(): List<KotlinFile> {
        log.trace { "Scanning $this..." }
        val files = mutableListOf<KotlinFile>()
        filesByPath[relativeTo(base)] = files
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
}
