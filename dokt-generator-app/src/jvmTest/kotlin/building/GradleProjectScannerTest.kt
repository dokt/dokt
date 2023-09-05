package app.dokt.generator.building

import app.dokt.generator.building.gradle.ProjectScanner
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Path

class GradleProjectScannerTest : FunSpec({
    test("dokt") {
        ProjectScanner(Path.of("..")).apply {
            scan()
            report() shouldBe """
                dokt-application = (APPLICATION, true)
                dokt-common = (INFRASTRUCTURE, true)
                dokt-domain = (DOMAIN, true)
                dokt-domain-test = (INFRASTRUCTURE, true)
                dokt-generator-app = (APPLICATION, true)
                dokt-gradle = (INFRASTRUCTURE, true)
                dokt-infrastructure = (INFRASTRUCTURE, true)
                dokt-interface = (INTERFACE, true)
                dokt-ktor-server = (KTOR_SERVER, true)
                dokt-swing = (SWING, true)
                dokt-test = (INFRASTRUCTURE, true)
            """.trimIndent()
        }
    }
    test("examples") {
        ProjectScanner(Path.of("..", "examples")).apply {
            scan()
            report() shouldBe """
                erp-dom = (DOMAIN, true)
                file-dom = (DOMAIN, true)
                hash-dom = (DOMAIN, true)
                hello-dom = (DOMAIN, true)
                window-dom = (DOMAIN, true)
                window-simulator-app = (APPLICATION, true)
                window-simulator-swing = (SWING, true)
            """.trimIndent()
        }
    }
})
