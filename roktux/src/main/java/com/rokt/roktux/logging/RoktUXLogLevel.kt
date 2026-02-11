package com.rokt.roktux.logging

/**
 * Log levels for RoktUXHelper, ordered from most to least verbose.
 *
 * Use these levels to control the verbosity of console logging output:
 * - [VERBOSE]: Detailed diagnostic information for deep debugging
 * - [DEBUG]: Development-time information like state changes
 * - [INFO]: General operational events
 * - [WARNING]: Recoverable issues that don't prevent operation
 * - [ERROR]: Failures that prevent expected behavior
 * - [NONE]: No logging (default for production)
 *
 * @property priority Integer priority where lower values are more verbose.
 */
// Suppress as this needs to be part of the public API
@Suppress("RedundantVisibilityModifier")
public enum class RoktUXLogLevel(public val priority: Int) {
    VERBOSE(0),
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    NONE(5),
    ;

    public companion object {
        /**
         * Returns the [RoktUXLogLevel] matching the given [priority],
         * or [NONE] if no match is found.
         */
        @JvmStatic
        public fun fromPriority(priority: Int): RoktUXLogLevel =
            values().firstOrNull { it.priority == priority } ?: NONE
    }
}
