package app.dokt.generator.code.psi

import io.kotest.core.spec.style.FunSpec
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

class PsiModificationTest : FunSpec({
    test("createWhitespace") {
        val project = KotlinCoreEnvironment.createForProduction(
            Disposer.newDisposable(),
            CompilerConfiguration(),
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        ).project
        val file = PsiFileFactory.getInstance(project).createFileFromText("hello.kt", KotlinFileType.INSTANCE,
            "fun main() { println(\"Hello world!\") }") as KtFile
        val factory = KtPsiFactory(project)

        file.add(factory.createWhiteSpace())
    }
})
