package app.dokt.gradle.domain.task

import app.dokt.gradle.common.task.Generate

abstract class GenerateCommonTest : Generate(
    GenerateCommonTest::class,
    "Generate base classes for unit tests.") {
    override fun generate() {
    }
}