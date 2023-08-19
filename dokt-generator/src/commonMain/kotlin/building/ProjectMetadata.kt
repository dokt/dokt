package app.dokt.generator.building

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Use ProjectType")
data class ProjectMetadata(val path: String, val type: ProjectType) {

}