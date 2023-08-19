package app.dokt.generator.building

import app.dokt.infra.Logger
import java.nio.file.Path

class GradleProjectScanner : Logger({}) {
    val projects: MutableMap<Path, ProjectMetadata> = mutableMapOf()

    fun scan(path: Path) {
        GradleProject.parse(path)
    }
}