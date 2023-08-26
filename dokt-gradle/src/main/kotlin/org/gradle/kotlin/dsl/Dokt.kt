package org.gradle.kotlin.dsl

import app.dokt.gradle.build.DoktExtension
import org.gradle.api.initialization.Settings

// This function is needed because Gradle doesn't generate accessors for settings extensions.
/** Configure Dokt */
fun Settings.dokt(action: DoktExtension.() -> Unit) = extensions.getByType(DoktExtension::class.java).action()
