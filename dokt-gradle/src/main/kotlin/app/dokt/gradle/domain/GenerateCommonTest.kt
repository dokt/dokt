package app.dokt.gradle.domain

import app.dokt.gradle.core.Generate

abstract class GenerateCommonTest : Generate(GenerateCommonTest::class,
    "Generate base classes for unit tests.") {
    override fun generate() {
    }
}