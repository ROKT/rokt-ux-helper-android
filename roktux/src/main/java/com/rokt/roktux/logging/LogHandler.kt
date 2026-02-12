package com.rokt.roktux.logging

/**
 * Abstraction for log output, used internally by [RoktUXLogger].
 *
 * The default implementation is [LogcatLogHandler], which writes to Android Logcat.
 * Replace via [RoktUXLogger.handler] in tests to capture log output.
 */
internal fun interface LogHandler {

    /**
     * Called when a log message passes the current level threshold.
     *
     * @param level The severity level of the message.
     * @param tag The log tag (e.g., "RoktUX").
     * @param message The log message content.
     * @param error An optional [Throwable] associated with the message.
     */
    fun log(level: RoktUXLogLevel, tag: String, message: String, error: Throwable?)
}
