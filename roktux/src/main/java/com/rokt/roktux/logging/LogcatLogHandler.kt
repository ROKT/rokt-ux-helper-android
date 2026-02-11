package com.rokt.roktux.logging

import android.util.Log

/**
 * Default [LogHandler] implementation that writes to Android Logcat
 * using [android.util.Log] methods mapped to [RoktUXLogLevel].
 */
public object LogcatLogHandler : LogHandler {

    override fun log(level: RoktUXLogLevel, tag: String, message: String, error: Throwable?) {
        when (level) {
            RoktUXLogLevel.VERBOSE -> Log.v(tag, message, error)
            RoktUXLogLevel.DEBUG -> Log.d(tag, message, error)
            RoktUXLogLevel.INFO -> Log.i(tag, message, error)
            RoktUXLogLevel.WARNING -> Log.w(tag, message, error)
            RoktUXLogLevel.ERROR -> Log.e(tag, message, error)
            RoktUXLogLevel.NONE -> { /* No output */ }
        }
    }
}
