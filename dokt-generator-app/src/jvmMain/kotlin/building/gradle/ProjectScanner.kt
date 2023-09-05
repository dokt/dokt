package app.dokt.generator.building.gradle

import app.dokt.common.visibleDirectories
import app.dokt.common.visibleDirectoryNames
import app.dokt.generator.building.ProjectType
import app.dokt.infra.Logger
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name

class ProjectScanner(private val root: Path) : Logger({}) {
    val subprojects = mutableMapOf<String, Pair<ProjectType, Boolean>>()

    private fun add(path: String, type: ProjectType, leaf: Boolean) {
        debug { "$path project is $type." }
        subprojects[path] = type to leaf
    }

    fun report() = subprojects.map { (path, type) -> "$path = $type" }.joinToString("\n")

    /**
     * Scan subprojects to include in settings. No need to include root and other parents.
     * @return This project has subproject.
     */
    fun scan(dir: Path = root, path: String = "", name: String = dir.name): Boolean {
        debug { "Detecting $dir" }
        var leaf = true
        when (val type = ProjectType.parse(path, name)) {
            null -> debug { "Ignored $dir" }
            ProjectType.INFRASTRUCTURE,
            ProjectType.INFRASTRUCTURE_JS,
            ProjectType.INFRASTRUCTURE_JVM -> {
                if (dir.resolve(SETTINGS_SCRIPT).exists()) warn { "Ignored another root at $dir!" }
                else {
                    debug { "Infrastructure may have subprojects" }
                    dir.visibleDirectories.forEach {
                        val childName = it.name
                        if (scan(it, "$path:$childName", childName)) leaf = false
                    }
                    add(path, type, leaf)
                }
            }
            ProjectType.DOMAINS -> {
                debug { "Expecting all subprojects to be domains." }
                dir.visibleDirectoryNames.filter { !ProjectType.ignore(it) }.forEach {
                    leaf = false
                    add("$path:$it", ProjectType.DOMAIN, true)
                }
            }
            ProjectType.ROOT -> {
                debug { "Excluding root and scanning subdirectories." }
                dir.visibleDirectories.forEach {
                    val childName = it.name
                    if (scan(it, childName, childName)) leaf = false
                }
            }
            else -> {
                add(path, type, true)
                debug { "No need to scan deeper." }
            }
        }
        return !leaf
    }
}
