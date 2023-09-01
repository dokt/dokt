package app.dokt.gradle.common

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import java.nio.file.Path

val Directory.path : Path get() = asFile.toPath()

val DirectoryProperty.path get() = get().path

val RegularFile.path: Path get() = asFile.toPath()

val RegularFileProperty.path get() = get().path
