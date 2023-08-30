package app.dokt.generator.building

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Path

class GradleProjectScannerTest : FunSpec({
    test("dokt") {
        GradleProjectScanner(Path.of("..")).apply {
            scan()
            report() shouldBe """
                :dokt-application = APPLICATION
                :dokt-common = INFRASTRUCTURE
                :dokt-domain = DOMAIN
                :dokt-domain-test = INFRASTRUCTURE
                :dokt-generator-app = APPLICATION
                :dokt-gradle = INFRASTRUCTURE
                :dokt-infrastructure = INFRASTRUCTURE
                :dokt-interface = INTERFACE
                :dokt-ktor-server = KTOR_SERVER
                :dokt-swing = SWING
                :dokt-test = INFRASTRUCTURE
            """.trimIndent()
        }
    }
    test("examples") {
        GradleProjectScanner(Path.of("..", "examples")).apply {
            scan()
            report() shouldBe """
                :erp-dom = DOMAIN
                :file-dom = DOMAIN
                :hash-dom = DOMAIN
                :hello-dom = DOMAIN
                :window-dom = DOMAIN
                :window-simulator-app = APPLICATION
                :window-simulator-swing = SWING
            """.trimIndent()
        }
    }
})
