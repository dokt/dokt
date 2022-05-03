package app.dokt.gradle

import org.gradle.api.Project

/*
logger.quiet("An info log message which is always logged.") // Logged in output stream by default
logger.error("An error log message.") // Logged in error stream by default
logger.warn("A warning log message.") // Logged in output stream by default
logger.lifecycle("A lifecycle info log message.") // Logged in output stream by default
logger.info("An info log message.") // Logged in output stream with --info option
logger.debug("A debug log message.") // Logged in output stream with --debug option
logger.trace("A trace log message.") // Never logged
*/
private fun format(level: String, path: String, message: String) = "$level app.dokt$path - $message"

/** Logged in output stream with --debug option */
fun formatDebug(message: String, path: String = "") = format("DEBUG", path, "$message.")

/** Logged in output stream with --info option */
fun formatInfo(message: String, path: String = "") = format("INFO ", path, "$message.")

/** Always logged in output stream by default */
fun formatQuiet(message: String, path: String = "") = format("QUIET", path, "$message.")

/** Logged in output stream by default */
fun formatWarn(message: String, path: String = "") = format("WARN ", path, "$message!")

fun Project.debug(message: String) = logger.debug(formatInfo(message, path))
fun Project.info(message: String) = logger.info(formatInfo(message, path))
fun Project.quiet(message: String) = logger.quiet(formatQuiet(message, path))
fun Project.warn(message: String) = logger.warn(formatWarn(message, path))
