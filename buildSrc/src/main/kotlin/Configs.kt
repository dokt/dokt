package org.gradle.kotlin.dsl

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

fun JavaPluginExtension.configureJava() {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

fun KotlinJvmCompilerOptions.configureCompiler() {
    jvmTarget.set(JvmTarget.JVM_11)
    allWarningsAsErrors.set(true)
}
