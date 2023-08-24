package app.dokt.gradle.common

import org.gradle.api.logging.Logger

abstract class LoggableAdapter : Loggable {
    protected abstract val logger: Logger

    private fun format(message: Any?) = message.toString()

    private fun format(message: () -> Any?) = format(message())

    //#region Log methods
    /** Always log error (the highest level) message to error stream. */
    override fun error(message: Any?) = logger.error(format(message))

    /** Always log error (the highest level) message to error stream. */
    override fun error(message: () -> Any?) = logger.error(format(message))

    /** Always log error (the highest level) message with throwable to error stream. */
    override fun error(throwable: Throwable?, message: Any?) = logger.error(format(message), throwable)

    /** Always log error (the highest level) message with throwable to error stream. */
    override fun error(throwable: Throwable?, message: () -> Any?) = logger.error(format(message), throwable)

    /**
     * Log important information (a higher level) message to output stream by default or `-q` or `--quiet` is switched.
     */
    override fun quiet(message: () -> Any?) {
        if (logger.isQuietEnabled) logger.quiet(format(message))
    }

    /**
     * Log important information (a higher level) message with throwable to output stream by default or `-q` or
     * `--quiet` is switched.
     */
    override fun quiet(throwable: Throwable?, message: () -> Any?) {
        if (logger.isQuietEnabled) logger.quiet(format(message), throwable)
    }

    /** Log warning (a high level) message to output stream by default or `-w` or `--warn` is switched. */
    override fun warn(message: () -> Any?) {
        if (logger.isWarnEnabled) logger.warn(format(message))
    }

    /**
     * Log warning (a high level) message with throwable to output stream by default or `-w` or `--warn` is switched.
     */
    override fun warn(throwable: Throwable?, message: () -> Any?) {
        if (logger.isWarnEnabled) logger.warn(format(message), throwable)
    }

    /** Log progress information (a mediocre level) message to output stream by default. */
    override fun lifecycle(message: () -> Any?) {
        if (logger.isLifecycleEnabled) logger.lifecycle("> ${message()}")
    }

    /** Log information (a low level) message to output stream if `-i` or `--info` is switched. */
    override fun info(message: () -> Any?) {
        if (logger.isInfoEnabled) logger.info(format(message))
    }

    /**
     * Log debug (the lowest level) message to output stream if `-d` or `--debug` is switched. Don't
     * [expose security sensitive information to the console](https://docs.gradle.org/current/userguide/logging.html#sec:debug_security)!
     */
    override fun debug(message: () -> Any?) {
        if (logger.isDebugEnabled) logger.debug(format(message))
    }
    //#endregion
}