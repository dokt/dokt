package app.dokt.generator.code

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile

class PsiTest : FunSpec({
    test("kts") {
        readKotlin("hello.kts", """print("Hello!")""").text shouldBe """print("Hello!")"""
    }
}) {
    companion object {
        private val environment = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.METADATA_CONFIG_FILES
        )
        private val manager = PsiManager.getInstance(environment.project)

        private fun read(filename: String, text: String) = when (filename.substringAfterLast('.')) {
            "kt", "kts" -> readKotlin(filename, text)
            else -> throw UnsupportedOperationException("The '$filename' file extension isn't supported!")
        }

        private fun readKotlin(filename: String, text: String) =
            manager.findFile(LightVirtualFile(filename, KotlinFileType.INSTANCE, text)) as KtFile
    }
}