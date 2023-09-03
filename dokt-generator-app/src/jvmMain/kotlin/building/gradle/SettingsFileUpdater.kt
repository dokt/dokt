package app.dokt.generator.building.gradle

import app.dokt.generator.code.editor.KotlinScriptEditor
import app.dokt.generator.code.psi.findCalls
import app.dokt.generator.code.psi.hasCall
import app.dokt.generator.code.psi.hasDotExpression
import app.dokt.generator.code.psi.stringValues

class SettingsFileUpdater(text: String) : KotlinScriptEditor(text, SETTINGS_SCRIPT, {}) {
    fun applyPlugins() = applyPlugins(DOKT_ID to APPLY_DOKT, REFRESH_ID to APPLY_REFRESH)

    fun manageDependencyResolutions(local: Boolean) = appendCall("dependencyResolutionManagement") { man ->
        var edited = man == null
        call(man, "repositories") {
            edited = it == null
            if (!it.hasCall("mavenCentral")) edited = true
            addStatement("mavenCentral()")
            if (local && !it.hasCall("mavenLocal")) {
                addStatement("mavenLocal()")
                edited = true
            }
        }
        edited
    }

    fun rootProject(name: String) = append {
        if (block.hasDotExpression("rootProject", "name")) false
        else {
            addStatement("rootProject.name = %S", name)
            true
        }
    }

    fun include(subprojects: List<String>) = append {
        val include = subprojects - block.findCalls("include").flatMap { it.stringValues }.toSet()
        addStatement("include(%L)", include.joinToString { "\"$it\"" })
        include.isNotEmpty()
    }

    fun SettingsInitialization.update(): Boolean {
        var edited = applyPlugins()
        if (cross) {
            if (manageDependencyResolutions(local)) edited = true
        }
        root?.let { if (rootProject(it)) edited = true }
        if (include.any()) if (include(include)) edited = true
        return edited
    }
}
