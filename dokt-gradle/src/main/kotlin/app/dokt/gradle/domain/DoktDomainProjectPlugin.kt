package app.dokt.gradle.domain

import app.dokt.gradle.*
import org.gradle.api.*

class DoktDomainProjectPlugin : DoktMultiplatformPlugin(DoktDomainProjectPlugin::class) {

    override fun Project.configure() {
        /*task("cleanGenerated", "Delete generated files.") { TODO Exists automatically when input & output is ok.
            delete(commonMainDir)
            delete(commonTestDir)
        }*/

        val generateDomain = register<GenerateCommonMain>()

        register<GenerateCommonTest>()

        register<GenerateDocumentation>()

        // TODO tasks.getByName("build") { it.dependsOn(generateDomain) }
    }
}
