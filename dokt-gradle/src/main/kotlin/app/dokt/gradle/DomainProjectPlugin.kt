package app.dokt.gradle

import app.dokt.generator.application.*
import app.dokt.generator.building.*
import org.gradle.api.*

@Suppress("unused")
class DomainProjectPlugin : Plugin<Project> {
    private val application by lazy { GradleApplication(project, multiProject) }
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    private val multiProject by lazy { GradleProject.parse(project.projectDir.toPath()) as MultiProject }
    private lateinit var project: Project

    override fun apply(target: Project) {
        project = target
        with(target) {
            task("cleanGenerated", "Delete generated files.") {
                delete(commonMainDir)
                delete(commonTestDir)
            }

            task(GENERATE_DOMAIN, "Generate application classes.") { coder.code() }

            task("generateDocumentation", "Generate Markdown documentation of the application.") {
                documentWriter.document()
            }

            tasks.filter { it.name.startsWith("compileKotlin") }.forEach { it.dependsOn(GENERATE_DOMAIN) }
        }
    }

    private fun Project.task(name: String, description: String, action: (Task) -> Unit): Task = task(name) {
        it.group = "dokt"
        it.description = description
        it.actions.add(action)
    }

    companion object {
        const val GENERATE_DOMAIN = "generateDomain"
    }
}
