package app.dokt.gradle.domain

import app.dokt.generator.application.*
import app.dokt.generator.building.*
import app.dokt.gradle.*
import app.dokt.gradle.core.ProjectPlugin
import org.gradle.api.*

@Suppress("unused")
class DomainProjectPlugin : ProjectPlugin(DomainProjectPlugin::class) {
    private val application by lazy { GradleApplication(project, multiProject) }
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    private val multiProject by lazy { GradleProject.parse(project.projectDir.toPath()) as MultiProject }
    private lateinit var project: Project

    override fun Project.configure() {
        task("cleanGenerated", "Delete generated files.") {
            delete(commonMainDir)
            delete(commonTestDir)
        }

        /*val generateCommonTestTask = task("generateDomainCommonTest",
            "Generate base classes for domain unit tests.") {
            coder.code()
        }*/

        val generateTask = task("generateDomain", "Generate application classes.") {
            coder.code()
        }

        task("generateDocumentation", "Generate Markdown documentation of the application.") {
            documentWriter.document()
        }

        tasks.getByName("build") { it.dependsOn(generateTask) }
    }

    private fun Project.task(name: String, description: String, action: (Task) -> Unit): Task = task(name) {
        it.group = "dokt"
        it.description = description
        it.actions.add(action)
    }
}
