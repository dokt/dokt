package app.dokt.generator.code.psi

import app.dokt.infra.Logger
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*

/** PSI environment */
open class Env(
    // TODO different environments for JVM, JS, Native and WASM
    environment: KotlinCoreEnvironment = KotlinCoreEnvironment.createForProduction(
        Disposer.newDisposable(),
        CompilerConfiguration(),
        EnvironmentConfigFiles.METADATA_CONFIG_FILES
    )
) : Logger({}) {
    private val project = environment.project

    val documentManager by lazy { PsiDocumentManager.getInstance(project) }

    val factory by lazy { KtPsiFactory(project) }

    private val fileFactory by lazy { PsiFileFactory.getInstance(project) }

    private val manager = PsiManager.getInstance(project)

    fun parse(filename: String, text: String) =
        manager.findFile(LightVirtualFile(filename, KotlinFileType.INSTANCE, text)) as KtFile

    fun parseKt(text: String) = parse("a.kt", text)

    fun parseKts(text: String) = parse("a.kts", text)

    fun create(filename: String, text: String) = fileFactory.createFileFromText(filename, KotlinFileType.INSTANCE, text)
}
