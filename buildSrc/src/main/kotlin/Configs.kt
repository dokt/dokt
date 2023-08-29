package org.gradle.kotlin.dsl

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

val javaCompatibility = JavaVersion.VERSION_11

fun KotlinJvmCompilerOptions.configureCompiler() {
    jvmTarget.set(JvmTarget.JVM_11)
    allWarningsAsErrors.set(true)
}
