package app.dokt.generator.building.gradle

import app.dokt.generator.code.editor.KotlinScriptEditor
import app.dokt.generator.code.psi.booleanAssigns
import app.dokt.generator.code.psi.findCall
import app.dokt.generator.code.psi.findCalls
import app.dokt.generator.code.psi.hasCall
import app.dokt.generator.code.psi.hasDotExpression
import app.dokt.generator.code.psi.stringValues

private const val DOKT = "dokt"
private const val BUILD = "useOnlyBuildFile"
private const val LOCAL = "useMavenLocal"
private const val MEM = "inMemoryInitialization"
private const val CROSS = "useCrossProjectDependencies"


class SettingsFileUpdater(text: String) : KotlinScriptEditor(text, SETTINGS_SCRIPT, {}) {
    private val extension by lazy { block.findCall(DOKT).booleanAssigns }

    private val build by lazy { extension[BUILD] ?: false }
    private val local by lazy { extension[LOCAL] ?: false }
    private val mem by lazy { extension[MEM] ?: false }
    private val cross by lazy { extension[CROSS] ?: false }

    fun applyPlugins() = applyPlugins(DOKT_ID to APPLY_DOKT, REFRESH_ID to APPLY_REFRESH)

    fun dokt() = appendCall(DOKT) {
        var edited = false
        mapOf(MEM to mem, CROSS to cross, LOCAL to local, BUILD to build).forEach { (prop, value) ->
            addStatement("$prop = $value")
            if (value != extension[prop]) edited = true
        }
        edited
    }

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

    fun include(subprojects: Set<String>) = append {
        val include = subprojects - block.findCalls("include").flatMap { it.stringValues }.toSet()
        addStatement("include(%L)", include.joinToString { "\"$it\"" })
        include.isNotEmpty()
    }

    fun update(root: String? = null, include: Set<String> = emptySet()): Boolean {
        var edited = applyPlugins()
        if (cross) {
            if (manageDependencyResolutions(local)) edited = true
        }
        root?.let { if (rootProject(it)) edited = true }
        if (include.any()) if (include(include)) edited = true
        return edited
    }
}
