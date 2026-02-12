package com.rokt.roktux.logging

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoktUXLoggerTest {

    private val capturedLogs = mutableListOf<CapturedLog>()
    private val testHandler = LogHandler { level, tag, message, error ->
        capturedLogs.add(CapturedLog(level, tag, message, error))
    }

    @Before
    fun setUp() {
        capturedLogs.clear()
        RoktUXLogger.handler = testHandler
        RoktUXLogger.logLevel = RoktUXLogLevel.NONE
    }

    @After
    fun tearDown() {
        RoktUXLogger.logLevel = RoktUXLogLevel.NONE
        RoktUXLogger.handler = LogcatLogHandler
    }

    @Test
    fun `default log level should be NONE`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.NONE

        // Act
        RoktUXLogger.verbose { "verbose message" }
        RoktUXLogger.debug { "debug message" }
        RoktUXLogger.info { "info message" }
        RoktUXLogger.warning { "warning message" }
        RoktUXLogger.error { "error message" }

        // Assert
        assertTrue(capturedLogs.isEmpty())
    }

    @Test
    fun `verbose level should capture all messages`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.VERBOSE

        // Act
        RoktUXLogger.verbose { "verbose" }
        RoktUXLogger.debug { "debug" }
        RoktUXLogger.info { "info" }
        RoktUXLogger.warning { "warning" }
        RoktUXLogger.error { "error" }

        // Assert
        assertEquals(5, capturedLogs.size)
        assertEquals(RoktUXLogLevel.VERBOSE, capturedLogs[0].level)
        assertEquals(RoktUXLogLevel.DEBUG, capturedLogs[1].level)
        assertEquals(RoktUXLogLevel.INFO, capturedLogs[2].level)
        assertEquals(RoktUXLogLevel.WARNING, capturedLogs[3].level)
        assertEquals(RoktUXLogLevel.ERROR, capturedLogs[4].level)
    }

    @Test
    fun `warning level should only capture warning and error`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.WARNING

        // Act
        RoktUXLogger.verbose { "verbose" }
        RoktUXLogger.debug { "debug" }
        RoktUXLogger.info { "info" }
        RoktUXLogger.warning { "warning" }
        RoktUXLogger.error { "error" }

        // Assert
        assertEquals(2, capturedLogs.size)
        assertEquals(RoktUXLogLevel.WARNING, capturedLogs[0].level)
        assertEquals(RoktUXLogLevel.ERROR, capturedLogs[1].level)
    }

    @Test
    fun `error level should only capture error messages`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.ERROR

        // Act
        RoktUXLogger.verbose { "verbose" }
        RoktUXLogger.debug { "debug" }
        RoktUXLogger.info { "info" }
        RoktUXLogger.warning { "warning" }
        RoktUXLogger.error { "error" }

        // Assert
        assertEquals(1, capturedLogs.size)
        assertEquals(RoktUXLogLevel.ERROR, capturedLogs[0].level)
        assertEquals("error", capturedLogs[0].message)
    }

    @Test
    fun `error should include throwable when provided`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.ERROR
        val exception = RuntimeException("test error")

        // Act
        RoktUXLogger.error(error = exception) { "something failed" }

        // Assert
        assertEquals(1, capturedLogs.size)
        assertEquals("something failed", capturedLogs[0].message)
        assertEquals(exception, capturedLogs[0].error)
    }

    @Test
    fun `all messages should use RoktUX tag`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.VERBOSE

        // Act
        RoktUXLogger.info { "test" }

        // Assert
        assertEquals("RoktUX", capturedLogs[0].tag)
    }

    @Test
    fun `debug level should capture debug and above`() {
        // Arrange
        RoktUXLogger.logLevel = RoktUXLogLevel.DEBUG

        // Act
        RoktUXLogger.verbose { "verbose" }
        RoktUXLogger.debug { "debug" }
        RoktUXLogger.info { "info" }
        RoktUXLogger.warning { "warning" }
        RoktUXLogger.error { "error" }

        // Assert
        assertEquals(4, capturedLogs.size)
        assertEquals(RoktUXLogLevel.DEBUG, capturedLogs[0].level)
        assertEquals(RoktUXLogLevel.INFO, capturedLogs[1].level)
        assertEquals(RoktUXLogLevel.WARNING, capturedLogs[2].level)
        assertEquals(RoktUXLogLevel.ERROR, capturedLogs[3].level)
    }

    @Test
    fun `handler can be swapped at runtime`() {
        // Arrange
        val secondCapturedLogs = mutableListOf<CapturedLog>()
        val secondHandler = LogHandler { level, tag, message, error ->
            secondCapturedLogs.add(CapturedLog(level, tag, message, error))
        }
        RoktUXLogger.logLevel = RoktUXLogLevel.VERBOSE

        // Act
        RoktUXLogger.info { "first handler" }
        RoktUXLogger.handler = secondHandler
        RoktUXLogger.info { "second handler" }

        // Assert
        assertEquals(1, capturedLogs.size)
        assertEquals("first handler", capturedLogs[0].message)
        assertEquals(1, secondCapturedLogs.size)
        assertEquals("second handler", secondCapturedLogs[0].message)
    }

    private data class CapturedLog(
        val level: RoktUXLogLevel,
        val tag: String,
        val message: String,
        val error: Throwable?,
    )
}
