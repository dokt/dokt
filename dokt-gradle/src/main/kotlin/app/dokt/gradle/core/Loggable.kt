package app.dokt.gradle.core

/**
 * [Gradle logging API](https://docs.gradle.org/current/userguide/logging.html)
 *
 * Log levels
 * 1. ERROR     - Error messages
 * 2. QUIET     - Important information messages (Gradle specific)
 * 3. WARNING   - Warning messages
 * 4. LIFECYCLE - Progress information messages (Gradle specific)
 * 5. INFO      - Information messages
 * 6. DEBUG     - Debug messages
 * - Gradle never logs TRACE level logs!
 */
interface Loggable {
    /** Always log error (the highest level) message to error stream. */
    fun error(message: Any?)

    /** Always log error (the highest level) message with throwable to error stream. */
    fun error(throwable: Throwable?, message: Any?)

    /**
     * Log important information (a higher level) message to output stream by default or `-q` or `--quiet` is switched.
     */
    fun quiet(message: () -> Any?)

    /**
     * Log important information (a higher level) message with throwable to output stream by default or `-q` or
     * `--quiet` is switched.
     */
    fun quiet(throwable: Throwable?, message: () -> Any?)

    /** Log warning (a high level) message to output stream by default or `-w` or `--warn` is switched. */
    fun warn(message: () -> Any?)

    /**
     * Log warning (a high level) message with throwable to output stream by default or `-w` or `--warn` is switched.
     */
    fun warn(throwable: Throwable?, message: () -> Any?)

    /** Log progress information (a mediocre level) message to output stream by default. */
    fun lifecycle(message: () -> Any?)

    /** Log information (a low level) message to output stream if `-i` or `--info` is switched. */
    fun info(message: () -> Any?)

    /**
     * Log debug (the lowest level) message to output stream if `-d` or `--debug` is switched. Don't
     * [expose security sensitive information to the console](https://docs.gradle.org/current/userguide/logging.html#sec:debug_security)!
     */
    fun debug(message: () -> Any?)

    @Deprecated("Gradle never logs TRACE level logs", level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("debug(message)"))
    fun trace(message: () -> Any?) {
        throw UnsupportedOperationException()
    }
}