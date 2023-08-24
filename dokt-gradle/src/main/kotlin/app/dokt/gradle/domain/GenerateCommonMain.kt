package app.dokt.gradle.domain

import app.dokt.generator.application.KotlinPoetApplicationCoder
import app.dokt.generator.building.*
import app.dokt.gradle.GradleApplication
import app.dokt.gradle.common.Generate

abstract class GenerateCommonMain : Generate(GenerateCommonMain::class,
    "Generate command & event objects, aggregates and transactional services which handles them all.") {
    private val application by lazy { GradleApplication(project, multiProject) }
    private val coder by lazy { KotlinPoetApplicationCoder(application) }
    private val multiProject by lazy { GradleProject.parse(project.projectDir.toPath()) as MultiProject }

    override fun generate() {
        coder.code()
    }
}