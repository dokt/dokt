package org.gradle.kotlin.dsl

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

fun Project.configureDetekt(config: ConfigurableFileCollection) = config.setFrom("$rootDir/config/detekt/detekt.yml")
