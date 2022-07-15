package app.dokt.generator.building

import io.kotest.core.spec.style.FunSpec

class GradlePropertiesWriterTest : FunSpec({
    val instance = GradlePropertiesUpdater(GradleProjectTest.instance.dir)
    test("update") { instance.update() }
})