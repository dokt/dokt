package app.dokt.generator.building.gradle

import app.dokt.common.Version
import app.dokt.generator.code.psi.Context
import app.dokt.generator.code.psi.ScriptEditor
import app.dokt.generator.code.psi.findCall
import app.dokt.generator.code.psi.getBlock
import app.dokt.generator.code.psi.findBinaries
import app.dokt.generator.code.psi.hasCallValue

class PsiSettingsInitializationScript(context: Context) : ScriptEditor(context, {}), SettingsInitialization {
    private val pluginsBlock by lazy {
        (block.findCall("plugins") ?: TODO("Create plugins")).getBlock()
    }

    private val pluginBinaries by lazy { pluginsBlock.findBinaries() }

    override fun configureDependencyResolutions(useMavenLocal: Boolean) {
        TODO("Not yet implemented")
    }

    override fun applyPlugin(id: String, version: Version) {
        val binary = pluginBinaries.find { it.hasCallValue(id) }
        if (binary != null) {
            debug { "Plugin $id already applied." }

            binary.replace(createExpression("""id("foo") version "1.0.0""""))
            return
        }
        //val expression = createExpression("""id("$id") version "$version"""")
        //pluginsBlock.add(expression)

        /*if (pluginsBlock.lambdaArguments.isNotEmpty()) {
            val lastArgument = pluginsBlock.lambdaArguments.last()
            if (lastArgument.firstChild.text == "}") {
                lastArgument.parent.addBefore(newPluginEntry, lastArgument)
            } else {
                lastArgument.parent.addAfter(newPluginEntry, lastArgument)
            }
        } else {
            //pluginsBlock.addArgument(newPluginEntry)
        }*/
    }
}
