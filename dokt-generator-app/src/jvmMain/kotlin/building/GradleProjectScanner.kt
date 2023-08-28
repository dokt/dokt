package app.dokt.generator.building

import app.dokt.common.visibleDirectories
import app.dokt.common.visibleDirectoryNames
import app.dokt.infra.Logger
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

class GradleProjectScanner(private val root: Path) : Logger({}) {
    val projects = mutableMapOf<String, ProjectType>()

    private fun add(path: String, type: ProjectType) {
        debug { "$path project is $type." }
        projects[path] = type
    }

    fun report() = projects.map { (path, type) -> "$path = $type" }.joinToString("\n")

    fun scan(dir: Path = root, path: String = ":", name: String = dir.name) {
        debug { "Detecting $dir" }
        when (val type = ProjectType.parse(path, name)) {
            null -> debug { "Ignored $dir" }
            ProjectType.INFRASTRUCTURE,
            ProjectType.INFRASTRUCTURE_JS,
            ProjectType.INFRASTRUCTURE_JVM -> {
                if (dir.resolve(GradleSettingsUpdater.FILENAME).exists()) warn { "Ignored another root at $dir!" }
                else {
                    add(path, type)
                    debug { "Infrastructure may have subprojects" }
                    dir.visibleDirectories.forEach {
                        val childName = it.name
                        scan(it, "$path:$childName", childName)
                    }
                }
            }
            ProjectType.DOMAINS -> {
                add(path, type)
                debug { "Expecting all subprojects to be domains." }
                dir.visibleDirectoryNames.filter { !ProjectType.ignore(it) }.forEach {
                    add("$path:$it", ProjectType.DOMAIN)
                }
            }
            ProjectType.ROOT -> {
                debug { "Excluding root and scanning subdirectories." }
                dir.visibleDirectories.forEach {
                    val childName = it.name
                    scan(it, path + childName, childName)
                }
            }
            else -> {
                add(path, type)
                debug { "No need to scan deeper." }
            }
        }
    }
}
