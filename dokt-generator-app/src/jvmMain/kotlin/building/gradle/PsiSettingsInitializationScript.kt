package app.dokt.generator.building.gradle

import app.dokt.common.Version
import app.dokt.generator.code.psi.*

class PsiSettingsInitializationScript(context: Context) : ScriptEditor(context, {}), SettingsInitialization {
    override fun applyPlugin(id: String, version: Version) {
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
