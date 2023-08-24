package app.dokt.generator.building

import app.dokt.generator.code.*
import com.squareup.kotlinpoet.*
import kotlin.io.path.listDirectoryEntries

/**
 * Gradle Kotlin build script updater */
class GradleBuildWriter(val project: GradleProject): KotlinScriptGenerator(project, {}) {
    override val name = "build.gradle"

    override fun FileSpec.Builder.generateModel() {
        if (project.hasSources) {
            project.resolveDependencies()
            when (project) {
                is MultiProject -> {
                    addCode(project.plugins())
                    if (!GradleSettingsWriter.CENTRALIZED_REPOSITORY_DECLARATION) addCode(repositories())
                    addCode(project.kotlin())
                }
                is SingleProject -> {
                    addCode(project.plugins())
                    if (!GradleSettingsWriter.CENTRALIZED_REPOSITORY_DECLARATION) addCode(repositories())
                    addCode(project.kotlin())
                }
            }
        } else if (project.isRoot) { addStatement("plugins { id(%S) }", GROUP) }
        if (project.isRoot) {
            addStatement("tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }")
            if (Dokt.LOCAL) addCode(controlFlow("tasks.create(%S)", "copyDokt") {
                addStatement("group = %S", "dokt")
                addStatement("description = %S", "Copy Dokt libraries to root project as a workaround for KTIJ-22057.")
                controlFlow("doLast") {
                    controlFlow("copy") {
                        addStatement(
                            "from(%S)",
                            if (project.dir.parent.listDirectoryEntries("dokt").any()) "../dokt" else ".."
                        )
                        addStatement("include(%S)", "dokt-*/**")
                        addStatement("exclude(%S, %S, %S)", "dokt-g*", "*/build", "*/src/*Test")
                        addStatement("into(projectDir)")
                    }
                }
            })
        }
    }

    fun write() {
        //super.write()
        //file.deleteIfEmpty()
        project.children.forEach { GradleBuildWriter(it).write() }
    }

    private fun CodeBlock.Builder.application(mainClass: String?) =
        mainClass?.let { addStatement("application { mainClass.set(%S) }", it) }

    private fun CodeBlock.Builder.applyKotlin(pluginId: String) = addStatement("kotlin(%S)", pluginId)

    /** TODO shorter generated folder */
    private fun CodeBlock.Builder.generated(name: String) =
        addStatement("kotlin.srcDir(buildDir.resolve(%S))", "common$name")

    private fun CodeBlock.Builder.id(pluginId: String) = addStatement("id(%S)", pluginId)

    private fun CodeBlock.Builder.sourceSet(name: String, code: CodeBlock.Builder.() -> Unit) =
        controlFlow("val $name by getting", code = code)

    private fun CodeBlock.Builder.sourceSetDependencies(name: String, code: CodeBlock.Builder.() -> Unit) =
        controlFlow("sourceSets[\"$name\"].dependencies", code = code)

    private fun MultiProject.kotlin() = controlFlow("kotlin") {
        // TODO add JVM only when jvmMain exists or only commonMain
        // TODO handle hasJvmTests
        addStatement(
            if (hasTests) "jvm { testRuns[\"test\"].executionTask.configure { useJUnitPlatform() } }"
            else "jvm()"
        )

        if (isDomain) {
            controlFlow("sourceSets") {
                sourceSet("commonMain") {
                    dependencies {
                        implementation(Dokt.APPLICATION) // TODO This is added temporarily
                        //implementation(Dokt.DOMAIN)

                        GradleProject.domains.filter {
                                domain -> domain.name != name && domain.exports.any { src.common.main.imports(it) }
                        }.map { it.name }.sorted().forEach { implementationProject(":$it") }
                    }
                    generated("Main")
                }

                if (src.common.hasTests) {
                    sourceSet("commonTest") {
                        dependencies {
                            implementation(Dokt.DOMAIN_TEST)
                            //implementation(JSON)
                            implementation(KOTEST)
                            //implementation("io.kotest:kotest-runner-junit5-jvm")
                            commonTestLibraries.used(src.common.test) { forEach { implementation(it) } }
                        }
                        generated("Test")
                    }
                }

                if (src.jvm.hasSources) {
                    jvmMainLibraries.used(src.jvm) {
                        sourceSetDependencies("jvmMain") {
                            forEach { implementation(it) }
                        }
                    }
                }
            }
        } else {
            sourceSetDependencies("commonMain") {
                when (layer) {
                    Layer.APPLICATION -> implementation(Dokt.APPLICATION)
                    Layer.INFRASTRUCTURE -> implementation(Dokt.COMMON)
                    Layer.INTERFACE -> implementation(Dokt.INTERFACE) // TODO multiplatform interface?
                    else -> throw IllegalStateException()
                }
            }

            if (src.common.hasTests) {
                sourceSetDependencies("commonTest") {
                    implementation(Dokt.TEST)
                    //implementation(JSON)
                    implementation(KOTEST)
                    commonTestLibraries.used(src.common.test) { forEach { implementation(it) } }
                    runtime(LOGBACK)
                }
            }

            if (src.jvm.hasSources) {
                jvmMainLibraries.used(src.jvm) {
                    sourceSetDependencies("jvmMain") {
                        forEach { implementation(it) }
                    }
                }
            }

            if (src.jvm.hasTests) {
                sourceSetDependencies("jvmTest") {
                    implementation(Dokt.TEST)
                    //implementation(JSON)
                    implementation(KOTEST)
                    jvmTestLibraries.used(src.jvm.test) { forEach { implementation(it) } }
                    runtime(LOGBACK)
                }
            }
        }

        application(mainClass)
    }

    private fun MultiProject.plugins() = controlFlow("plugins") {
        if (isInterface && mainClass != null) addStatement("application")

        // TODO Is Maven really needed
        //addStatement("`maven-publish`")

        applyKotlin(platform.id)

        if (isDomain || imports(SERIALIZATION_PACKAGE)) applyKotlin(SERIALIZATION_ID)

        if (isDomain) id("$GROUP.domain")

        if (isRoot) id(GROUP)
    }

    private fun SingleProject.kotlin() = codeBlock {
        if (hasTests) addStatement("tasks.withType<Test>().configureEach { useJUnitPlatform() }")

        dependencies {
            implementation(if (isInterface) Dokt.INTERFACE else Dokt.COMMON)
            // TODO Do better searching
            if (isInterface) implementationProject(":${name.substringBeforeLast('-')}-app")
            if (isJvm) {
                jvmMainLibraries.forEach { (packagePrefix, dependency) ->
                    if (imports(packagePrefix)) implementation(dependency)
                }
            }
            if (isInterface && isJvm) runtime(LOGBACK)
            if (hasTests) {
                testImplementation(Dokt.TEST)
                if (isInterface && isJvm) testRuntime(LOGBACK)
            }
        }

        application(mainClass)
    }

    private fun SingleProject.plugins() = controlFlow("plugins") {
        if (isInterface && isJvm && mainClass != null) addStatement("application")

        // TODO Is Maven really needed
        //addStatement("`maven-publish`")

        applyKotlin(platform.id)

        if (imports(SERIALIZATION_PACKAGE)) applyKotlin(SERIALIZATION_ID)

        if (isRoot) id(GROUP)
    }

    companion object {
        private const val GROUP = "app.dokt"
        private const val IMPL = "implementation"
        private const val SERIALIZATION_ID = "plugin.serialization"
        private const val SERIALIZATION_PACKAGE = "kotlinx.serialization"
        private const val KOTEST = "Testing.kotest.runner.junit5"
        private const val LOGBACK = "ch.qos.logback:logback-classic"

        private val commonLibraries = mapOf(
            "com.benasher44.uuid" to "com.benasher44:uuid",
            "kotlin.time" to "KotlinX.datetime", // TODO Move from core to app
            "kotlinx.serialization" to "KotlinX.serialization.core" // TODO api(KotlinX.serialization.core) in dokt-domain doesn't work.
        )
        private val commonTestLibraries = mapOf(
            "io.mockk" to "Testing.mockK"
        )
        private val jvmMainLibraries = mapOf(
            "com.sun.jna.platform" to "net.java.dev.jna:jna-platform",
            "kotlin.time" to "KotlinX.datetime", // TODO Move from core to app
            "org.jfree.chart" to "org.jfree:jfreechart"
        )
        private val jvmTestLibraries = mapOf(
            "io.mockk" to "Testing.mockK"
        )

        private fun repositories() = controlFlow("repositories") {
            addStatement("mavenCentral()")
            // addStatement("mavenLocal()") TODO KTIJ-22057 use includeBuild("..") in settings.gradle instead.
        }

        private fun CodeBlock.Builder.dep(config: String, dep: String) =
            if (dep.contains(':')) addStatement("$config(%S)", "$dep:_")
            else addStatement("$config($dep)")

        private fun CodeBlock.Builder.dep(config: String, dokt: Dokt) =
            if (Dokt.LOCAL) depProject(config, ":${dokt.artifact}")
            else dep(config, "$GROUP:${dokt.artifact}")

        private fun CodeBlock.Builder.depProject(config: String, dep: String) =
            addStatement("$config(project(%S))", dep)

        private fun CodeBlock.Builder.dependencies(code: CodeBlock.Builder.() -> Unit) =
            controlFlow("dependencies", code = code)

        private fun CodeBlock.Builder.implementation(dep: String) = dep(IMPL, dep)

        private fun CodeBlock.Builder.implementation(dokt: Dokt) = dep(IMPL, dokt)

        private fun CodeBlock.Builder.implementationProject(dep: String) = depProject(IMPL, dep)

        private fun CodeBlock.Builder.runtime(dep: String) = dep("runtimeOnly", dep)

        private fun CodeBlock.Builder.testImplementation(dokt: Dokt) = dep("testImplementation", dokt)

        private fun CodeBlock.Builder.testRuntime(dep: String) = dep("testRuntimeOnly", dep)

        private fun Map<String, String>.used(importer: Importer, action: List<String>.() -> Unit) {
            val dependencies = keys.filter { importer.imports(it) }.map { getValue(it) }.sorted()
            if (dependencies.any()) dependencies.action()
        }
    }
}