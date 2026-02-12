package com.rokt.roktux.logging

/**
 * Logger for RoktUXHelper with configurable log levels.
 *
 * Set the log level via [com.rokt.roktux.RoktUx.setLogLevel] or directly on [logLevel].
 * All logging functions use inline lambdas so that message construction is skipped
 * entirely when the current [logLevel] would filter the message out.
 *
 * Example:
 * ```kotlin
 * RoktUXLogger.logLevel = RoktUXLogLevel.DEBUG
 * RoktUXLogger.debug { "Layout loaded successfully" }
 * ```
 */
internal object RoktUXLogger {

    /**
     * The current minimum log level. Messages below this level are not logged.
     * Default is [RoktUXLogLevel.NONE] (no logging).
     */
    @Volatile
    var logLevel: RoktUXLogLevel = RoktUXLogLevel.NONE

    /**
     * The log output backend. Default is [LogcatLogHandler].
     */
    @Volatile
    var handler: LogHandler = LogcatLogHandler

    /**
     * Logs a verbose message for detailed diagnostic information.
     * The [message] lambda is only evaluated if the current level permits verbose output.
     *
     * @param error Optional [Throwable] to include in the log output.
     * @param message Lambda producing the log message string.
     */
    inline fun verbose(error: Throwable? = null, message: () -> String) {
        if (logLevel.priority <= RoktUXLogLevel.VERBOSE.priority) {
            handler.log(RoktUXLogLevel.VERBOSE, TAG, message(), error)
        }
    }

    /**
     * Logs a debug message for development-time information.
     * The [message] lambda is only evaluated if the current level permits debug output.
     *
     * @param error Optional [Throwable] to include in the log output.
     * @param message Lambda producing the log message string.
     */
    inline fun debug(error: Throwable? = null, message: () -> String) {
        if (logLevel.priority <= RoktUXLogLevel.DEBUG.priority) {
            handler.log(RoktUXLogLevel.DEBUG, TAG, message(), error)
        }
    }

    /**
     * Logs an info message for general operational events.
     * The [message] lambda is only evaluated if the current level permits info output.
     *
     * @param error Optional [Throwable] to include in the log output.
     * @param message Lambda producing the log message string.
     */
    inline fun info(error: Throwable? = null, message: () -> String) {
        if (logLevel.priority <= RoktUXLogLevel.INFO.priority) {
            handler.log(RoktUXLogLevel.INFO, TAG, message(), error)
        }
    }

    /**
     * Logs a warning message for recoverable issues.
     * The [message] lambda is only evaluated if the current level permits warning output.
     *
     * @param error Optional [Throwable] to include in the log output.
     * @param message Lambda producing the log message string.
     */
    inline fun warning(error: Throwable? = null, message: () -> String) {
        if (logLevel.priority <= RoktUXLogLevel.WARNING.priority) {
            handler.log(RoktUXLogLevel.WARNING, TAG, message(), error)
        }
    }

    /**
     * Logs an error message for failures that prevent expected behavior.
     * The [message] lambda is only evaluated if the current level permits error output.
     *
     * @param error Optional [Throwable] to include in the log output.
     * @param message Lambda producing the log message string.
     */
    inline fun error(error: Throwable? = null, message: () -> String) {
        if (logLevel.priority <= RoktUXLogLevel.ERROR.priority) {
            handler.log(RoktUXLogLevel.ERROR, TAG, message(), error)
        }
    }

    internal const val TAG: String = "RoktUX"
}
