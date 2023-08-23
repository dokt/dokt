package app.dokt.gradle.domain

import app.dokt.generator.application.MarkDownApplicationDocumentWriter
import app.dokt.generator.building.*
import app.dokt.gradle.GradleApplication
import app.dokt.gradle.core.Generate

abstract class GenerateDocumentation : Generate(GenerateDocumentation::class,
    "Generate Markdown documentation of this domain.") {
    private val application by lazy { GradleApplication(project, multiProject) }
    private val documentWriter by lazy { MarkDownApplicationDocumentWriter(application) }
    private val multiProject by lazy { GradleProject.parse(project.projectDir.toPath()) as MultiProject }

    override fun generate() {
        documentWriter.document()
    }
}