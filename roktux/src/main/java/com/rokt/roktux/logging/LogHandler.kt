package com.rokt.roktux.logging

/**
 * Abstraction for log output.
 *
 * Implement this interface to redirect Rokt UX log messages to a custom
 * destination (e.g., file, remote service, or test capture).
 *
 * The default implementation is [LogcatLogHandler], which writes to Android Logcat.
 */
public fun interface LogHandler {

    /**
     * Called when a log message passes the current level threshold.
     *
     * @param level The severity level of the message.
     * @param tag The log tag (e.g., "RoktUX").
     * @param message The log message content.
     * @param error An optional [Throwable] associated with the message.
     */
    public fun log(level: RoktUXLogLevel, tag: String, message: String, error: Throwable?)
}
