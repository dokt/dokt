package app.dokt.generator.building.psi

import app.dokt.common.Version
import app.dokt.generator.building.GradleSettingsScriptUpdater
import app.dokt.generator.code.psi.*
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementWalkingVisitor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

class GradleSettingsScriptPsiUpdater(file: KtFile, env: Env = Psi) : ScriptUpdater({}, file, env),
    GradleSettingsScriptUpdater
{
    override fun addPlugin(id: String, version: Version) {
        val pluginsBlock = block.findCall("plugins")
        println(pluginsBlock?.text)
        pluginsBlock?.findCalls("id")?.forEach {
            println("->${it.text}")
        }
            /*.filterIsInstance<KtCallExpression>()
            .find { it.name == "plugins" }
        info { pluginsBlock?.text }*/

        /*if (pluginsBlock  != null) {
            val newPluginEntry = createExpression("id(\"$id\") version \"$version\"")

            if (pluginsBlock.lambdaArguments.isNotEmpty()) {
                val lastArgument = pluginsBlock.lambdaArguments.last()
                if (lastArgument.firstChild.text == "}") {
                    lastArgument.parent.addBefore(newPluginEntry, lastArgument)
                } else {
                    lastArgument.parent.addAfter(newPluginEntry, lastArgument)
                }
            } else {
                //pluginsBlock.addAaddArgument(newPluginEntry)
            }
        }*/
    }
}
