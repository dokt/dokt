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
            task("cleanGenerated") {
                it.actions.add {
                    delete(commonMainDir)
                    delete(commonTestDir)
                }
            }.description = "Delete generated files."

            task(GENERATE_DOMAIN) {
                it.actions.add {
                    coder.code()
                }
            }.description = "Generate application classes."

            task("generateDocumentation") {
                it.actions.add {
                    documentWriter.document()
                }
            }.description = "Generate Markdown documentation of the application."

            tasks.filter { it.name.startsWith("compileKotlin") }.forEach { it.dependsOn(GENERATE_DOMAIN) }
        }
    }

    companion object {
        const val GENERATE_DOMAIN = "generateDomain"
    }
}
